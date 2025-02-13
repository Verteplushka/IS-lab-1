package bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;

@RequestScoped
public class ErrorBean {

    public void sendError() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
//        facesContext.responseComplete(); // Завершаем обработку запроса
//        try {
//            facesContext.getExternalContext().responseSendError(404, "Внутренняя ошибка сервера");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        facesContext.responseComplete();
    }
}
