package com.example.tutoringweb;

import com.example.support.TutoringDAO;
import com.example.support.tutoring_classes.Day;
import com.example.support.tutoring_classes.Teach;
import com.example.support.tutoring_classes.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "get", value = "/api/get")
public class Get extends HttpServlet {
    public void init() {
        TutoringDAO.openConnection();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        HttpSession session = request.getSession(false);
        User thisUser = null;

        if(session != null)
            thisUser = (User) session.getAttribute("user");

        if (request.getParameter("type") != null || request.getParameter("type").equals("")) {
            if (request.getParameter("type").equals("bookings")) {
                if (thisUser != null) {
                    if (thisUser.isAdmin())
                        gson.toJson(TutoringDAO.getBookings(), response.getWriter());
                    else {
                        gson.toJson(TutoringDAO.getBookings(thisUser.getUsername()), response.getWriter());
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
            } else if (request.getParameter("type").equals("courses")) {
                gson.toJson(TutoringDAO.getCourses(), response.getWriter());
            } else if (request.getParameter("type").equals("teaches")) {
                gson.toJson(TutoringDAO.getTeaches(), response.getWriter());
            } else if (request.getParameter("type").equals("teachers")) {
                gson.toJson(TutoringDAO.getTeachers(), response.getWriter());
            } else if (request.getParameter("type").equals("isBusy")) {
                if (request.getParameter("teacherId") != null) {
                    int teacherID = Integer.parseInt(request.getParameter("teacherId"));
                    Boolean[][] bookable = new Boolean[5][4];
                    String[] hour = {"a", "b", "c", "d"};

                    for (int iD = 0; iD < 5; iD++) {
                        for (int iH = 0; iH < 4; iH++) {
                            bookable[iD][iH] = !TutoringDAO.isBusy(teacherID, iD, hour[iH]);
                        }
                    }
                    response.getWriter().println(gson.toJson(bookable));
                }
            } else if (request.getParameter("type").equals("teachesOfDay")) {
                if (request.getParameter("day") != null && !request.getParameter("day").equals("")) {
                    int dayNumber = Integer.parseInt(request.getParameter("day"));

                    if (dayNumber < 0 || dayNumber > 4)
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);

                    if (thisUser != null && !thisUser.isAdmin()) {
                        List<Teach> teaches = TutoringDAO.getTeaches();
                        Day day;
                        List<Teach> a = new ArrayList<>();
                        List<Teach> b = new ArrayList<>();
                        List<Teach> c = new ArrayList<>();
                        List<Teach> d = new ArrayList<>();

                        for (Teach teach : teaches) {
                            if (!TutoringDAO.areBusy(teach.getTeacher().getId(), thisUser.getUsername(), dayNumber, "a")) {
                                a.add(teach);
                            }
                            if (!TutoringDAO.areBusy(teach.getTeacher().getId(), thisUser.getUsername(), dayNumber, "b")) {
                                b.add(teach);
                            }
                            if (!TutoringDAO.areBusy(teach.getTeacher().getId(), thisUser.getUsername(), dayNumber, "c")) {
                                c.add(teach);
                            }
                            if (!TutoringDAO.areBusy(teach.getTeacher().getId(), thisUser.getUsername(), dayNumber, "d")) {
                                d.add(teach);
                            }
                        }

                        day = new Day(a, b, c, d);

                        gson.toJson(day, response.getWriter());
                    } else if (thisUser == null || thisUser.isAdmin()) {
                        List<Teach> teaches = TutoringDAO.getTeaches();
                        Day day;
                        List<Teach> a = new ArrayList<>();
                        List<Teach> b = new ArrayList<>();
                        List<Teach> c = new ArrayList<>();
                        List<Teach> d = new ArrayList<>();

                        for (Teach teach : teaches) {
                            if (!TutoringDAO.isBusy(teach.getTeacher().getId(), dayNumber, "a")) {
                                a.add(teach);
                            }
                            if (!TutoringDAO.isBusy(teach.getTeacher().getId(), dayNumber, "b")) {
                                b.add(teach);
                            }
                            if (!TutoringDAO.isBusy(teach.getTeacher().getId(), dayNumber, "c")) {
                                c.add(teach);
                            }
                            if (!TutoringDAO.isBusy(teach.getTeacher().getId(), dayNumber, "d")) {
                                d.add(teach);
                            }
                        }

                        day = new Day(a, b, c, d);

                        gson.toJson(day, response.getWriter());
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    public void destroy() {
        TutoringDAO.closeConnection();
    }
}