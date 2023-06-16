package com.example.tutoringweb;

import com.example.support.TutoringDAO;
import com.example.support.tutoring_classes.Booking;
import com.example.support.tutoring_classes.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.List;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "session", value = "/session")
public class Session extends HttpServlet {
    public void init() {
        TutoringDAO.openConnection();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (request.getParameter("logout") != null) {
            session.invalidate();
            JsonObject success = new JsonObject();
            success.addProperty("status", "success");
            response.getWriter().println(success);
        } else if(session.getAttribute("user") != null){
            response.getWriter().println(gson.toJson(session.getAttribute("user")));
        }else{
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        HttpSession session;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        User user;
        if ((user = TutoringDAO.checkUser(request.getParameter("username"), request.getParameter("password"))) != null) {
            session = request.getSession();
            session.setAttribute("user", user);

            response.getWriter().println(gson.toJson(user));
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }


    public void destroy() {
        TutoringDAO.closeConnection();
    }
}