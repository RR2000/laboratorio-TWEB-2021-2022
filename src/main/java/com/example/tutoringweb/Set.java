package com.example.tutoringweb;

import com.example.support.TutoringDAO;
import com.example.support.tutoring_classes.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "set", value = "/api/set")
public class Set extends HttpServlet {
    public void init() {
        TutoringDAO.openConnection();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        User thisUser = null;

        if (session != null)
            thisUser = (User) session.getAttribute("user");

        JsonObject success = new JsonObject();
        success.addProperty("status", "success");

        if (request.getParameter("type") == null || request.getParameter("type").equals("")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (thisUser == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            switch (request.getParameter("type")) {
                case "book": {
                    if (!thisUser.isAdmin()) {
                        if (request.getParameter("teacherId") != null &&
                                request.getParameter("courseId") != null &&
                                request.getParameter("day") != null &&
                                request.getParameter("hour") != null &&
                                !request.getParameter("teacherId").isEmpty() &&
                                !request.getParameter("courseId").isEmpty() &&
                                !request.getParameter("day").isEmpty() &&
                                !request.getParameter("hour").isEmpty()) {
                            int teacherId = Integer.parseInt(request.getParameter("teacherId"));
                            int courseId = Integer.parseInt(request.getParameter("courseId"));
                            String userId = thisUser.getUsername();
                            int day = Integer.parseInt(request.getParameter("day"));
                            String hour = request.getParameter("hour");

                            if (TutoringDAO.setBook(teacherId, courseId, userId, day, hour, "booked")) {
                                response.getWriter().println(success);
                                System.out.println("Prenotata");
                            } else {
                                response.sendError(HttpServletResponse.SC_CONFLICT);
                            }
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can't execute this command as admin.");
                    }
                    break;
                }
                case "course": {
                    if (thisUser.isAdmin()) {
                        if (TutoringDAO.setCourse(
                                request.getParameter("name"),
                                request.getParameter("description")
                        )) {
                            response.getWriter().println(success);
                        } else {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                    break;
                }
                case "teach": {
                    if (thisUser.isAdmin()) {
                        if (TutoringDAO.setTeach(
                                Integer.parseInt(request.getParameter("teacherId")),
                                Integer.parseInt(request.getParameter("courseId"))
                        )) {
                            response.getWriter().println(success);
                        } else {
                            response.sendError(HttpServletResponse.SC_CONFLICT);
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                    break;
                }
                case "teacher": {
                    if (thisUser.isAdmin()) {
                        if (TutoringDAO.setTeacher(
                                request.getParameter("name"),
                                request.getParameter("surname")
                        )) {
                            response.getWriter().println(success);
                        } else {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                    break;
                }
                case "updateStatus": {
                    String statusBefore = TutoringDAO.getStatus(Integer.parseInt(request.getParameter("id")));

                    if (statusBefore.equals("booked")) {
                        TutoringDAO.updateStatus(Integer.parseInt(request.getParameter("id")), request.getParameter("status"));
                        response.getWriter().println(success);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
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