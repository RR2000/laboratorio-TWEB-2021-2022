package com.example.tutoringweb;

import com.example.support.TutoringDAO;
import com.example.support.tutoring_classes.User;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "delete", value = "/api/delete")
public class Delete extends HttpServlet {
    public void init() {
        TutoringDAO.openConnection();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        User thisUser = (User) session.getAttribute("user");

        JsonObject success = new JsonObject();
        success.addProperty("status", "success");

        if (request.getParameter("type") == null || request.getParameter("type").equals("")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (thisUser == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            switch (request.getParameter("type")) {
                case "course": {
                    if (thisUser.isAdmin()) {
                        TutoringDAO.deleteCourse(Integer.parseInt(request.getParameter("id")));
                        response.getWriter().println(success);
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                    break;
                }
                case "teach": {
                    if (thisUser.isAdmin()) {
                        TutoringDAO.deleteTeach(
                                Integer.parseInt(request.getParameter("teacherId")),
                                Integer.parseInt(request.getParameter("courseId"))
                        );

                        response.getWriter().println(success);

                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                    break;
                }
                case "teacher": {
                    if (thisUser.isAdmin()) {
                        TutoringDAO.deleteTeacher(Integer.parseInt(request.getParameter("id")));
                        response.getWriter().println(success);

                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                    break;
                }
                case "updateStatus": {
                    TutoringDAO.updateStatus(Integer.parseInt(request.getParameter("id")), request.getParameter("status"));
                    response.getWriter().println(success);
                    break;
                }
                default: {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    break;
                }
            }
        }
    }


    public void destroy() {
        TutoringDAO.closeConnection();
    }
}