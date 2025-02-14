package bean;

import entity.*;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;
import lombok.Getter;
import lombok.Setter;
import service.ImportHistoryService;
import service.ProductService;
import util.CsvParser;

import java.io.*;
import java.net.ConnectException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
@Getter
@Setter
public class ImportBean implements Serializable {
    @Inject
    private ErrorBean errorBean;
    @Inject
    private ImportHistoryService importHistoryService;
    @Inject
    private ProductService productService;
    @Inject
    private UserBean userBean;
    @Resource
    private UserTransaction utx;
    private final MinioClient minioClient;
    private final String bucketName = "import-files";

    public ImportBean() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    @PostConstruct
    public void init() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                System.out.println("Bucket " + bucketName + " was created in MinIO.");
            } else {
                System.out.println("Bucket " + bucketName + " already exists.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: MinIO is unavailable. Bucket check failed: " + e.getMessage());
//            FacesContext.getCurrentInstance().addMessage(null,
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Warning! File storage is not available!", null));
        }
    }


    public List<ImportHistory> getAllImportHistory() {
        List<ImportHistory> history = importHistoryService.getAllImportHistory();
        if (userBean.getUser().getRole() == User.Role.ADMIN) {
            return history;
        }
        return history.stream()
                .filter(each_history -> each_history.getUser().equals(userBean.getUser().getLogin()))
                .collect(Collectors.toList());
    }

    private Part file;

    public void importProducts() throws IOException {
        if (file == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No file uploaded!", null));
            return;
        }

        InputStream fileContent = file.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent));
        StringBuilder fileContentStr = new StringBuilder();
        String line;
        boolean isFirstLine = true;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }
            fileContentStr.append(line).append("\n");
        }

        CsvParser parser = new CsvParser();

        ImportHistory savedHistory = importHistoryService.save(new ImportHistory(OperationStatus.FAILED, userBean.getUser().getLogin(), null));

        try {
            List<Product> products = parser.parseCSV(file);

            utx.begin();

            productService.saveAll(products, userBean.getUser());

            String objectPath = "imports/" + savedHistory.getId() + ".csv";

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectPath)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType("application/octet-stream")
                                .build()
                );
            }

            utx.commit();

            savedHistory.setStatus(OperationStatus.SUCCESS);
            savedHistory.setObjectsAdded(products.size());
            importHistoryService.update(savedHistory);

        } catch (ConnectException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occurred: File storage is not available.", null));
            System.out.println("Error occurred: " + e.getMessage() + " class: " + e.getClass());
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occurred: " + e.getMessage(), null));
            System.out.println("Error occurred: " + e.getMessage() + " class: " + e.getClass());
        } finally {
            try {
                utx.rollback();
            } catch (Exception rollbackEx) {
                System.err.println("Error during transactional rollback: " + rollbackEx.getMessage());
            }
            errorBean.sendError();
        }
    }


    public void downloadFile(Long importId) {
        String objectPath = "imports/" + importId + ".csv";

        try {
            InputStream fileStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .build()
            );

            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + importId + ".csv");

            try (OutputStream out = response.getOutputStream()) {
                fileStream.transferTo(out);
            }

            FacesContext.getCurrentInstance().responseComplete();

        } catch (ConnectException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error downloading file: File storage is not available.", null));
            System.out.println("Error downloading file: " + e.getMessage() + " class: " + e.getClass());

        } catch (ErrorResponseException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error downloading file: File with this id does not exist in file storage.", null));
            System.out.println("Error downloading file: " + e.getMessage() + " class: " + e.getClass());

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error downloading file: " + e.getMessage(), null));
            System.out.println("Error downloading file: " + e.getMessage() + " class: " + e.getClass());
        }
    }

}

