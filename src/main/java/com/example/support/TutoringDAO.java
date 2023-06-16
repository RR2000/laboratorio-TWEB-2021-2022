package com.example.support;

import com.example.support.tutoring_classes.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TutoringDAO {
    private static final String url = "jdbc:mysql://db:3306/tutoring";
    private static final String usr = "root";
    private static final String pwd = "example";
    private static Connection connection = null;

    public static void openConnection() {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            System.out.println("Driver correttamente registrato"); //TODO migliorare
            connection = DriverManager.getConnection(url, usr, pwd);
            System.out.println("Connessione eseguita correttamente");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Disconnessione eseguita correttamente"); //TODO
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        if (connection == null) {
            openConnection();
        }
        try {
            resultSet = connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            System.err.println(query);
            e.printStackTrace();
        }
        return resultSet;

    }

    private static void genericExecuteUpdate(String query) {
        if (connection == null) {
            openConnection();
        }

        try {
            connection.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            System.err.println(query);
            e.printStackTrace();
        }

    }

    private static void executeUpdate(String query) throws SQLException {
        if (connection == null) {
            openConnection();
        }

        System.out.println(query);
        connection.createStatement().executeUpdate(query);
    }

    public static List<Teacher> getTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        ResultSet res = executeQuery("SELECT * FROM teachers WHERE available = 1");

        try {
            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
                String surname = res.getString("surname");
                teachers.add(new Teacher(id, name, surname));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teachers;
    }

    public static boolean setTeacher(String name, String surname) {
        boolean res = true;

        if (!name.isEmpty() && !surname.isEmpty()) {
            String n = "'" + name + "'";
            String s = "'" + surname + "'";
            try {
                executeUpdate("INSERT INTO teachers (name, surname) " +
                        "VALUES (" + n + "," + s + ");");
            } catch (SQLIntegrityConstraintViolationException e) {
                res = false;
            } catch (SQLException e) {
                res = false;
                e.printStackTrace();
            }
        } else {
            res = false;
        }

        return res;
    }

    public static void deleteTeacher(int id) {
        try {
            executeUpdate("UPDATE teachers " +
                    "SET available = '0' " +
                    "WHERE id = " + id);
            executeUpdate("UPDATE teaches " +
                    "SET available = '0' " +
                    "WHERE teacherId = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Course> getCourses() {
        List<Course> courses = new ArrayList<>();
        ResultSet res = executeQuery("SELECT * FROM courses WHERE available = '1'");

        try {
            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
                String description = res.getString("description");
                courses.add(new Course(id, name, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    public static boolean setCourse(String name, String description) {
        boolean res = true;

        if (!name.isEmpty()) {
            String n = "'" + name + "'";
            String d = "'" + description + "'";
            try {
                executeUpdate("INSERT INTO courses (name, description) " +
                        "VALUES (" + n + "," + d + ");");
            } catch (SQLIntegrityConstraintViolationException e) {
                res = false;
            } catch (SQLException e) {
                res = false;
                e.printStackTrace();
            }
        } else {
            res = false;
        }

        return res;
    }

    public static void deleteCourse(int id) {
        /*genericExecuteUpdate("DELETE FROM courses WHERE id = " + id);*/
        try {
            executeUpdate("UPDATE courses " +
                    "SET available = '0' " +
                    "WHERE id = " + id);
            executeUpdate("UPDATE teaches " +
                    "SET available = '0' " +
                    "WHERE courseId = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User getUser(String username) {
        User user = null;
        ResultSet res = executeQuery("SELECT * FROM users WHERE username='" + username + "'");

        try {
            while (res.next()) {
                String name = res.getString("name");
                String surname = res.getString("surname");
                Boolean admin = res.getBoolean("admin");
                user = new User(username, name, surname, admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static void setUser(User user, String password) {
        String u = "'" + user.getUsername() + "', ";
        String n = "'" + user.getName() + "', ";
        String s = "'" + user.getSurname() + "', ";
        String p = "'" + password + "', ";
        String a = "'" + String.valueOf(user.isAdmin() ? 1 : 0) + "'";

        genericExecuteUpdate("INSERT INTO users (username, name, surname, password, admin) " +
                "VALUES (" + u + n + s + p + a + ");");
    }

    public static User checkUser(String username, String password) {
        User user = null;
        ResultSet res = executeQuery("SELECT * FROM users WHERE username='" + username + "' AND password = '" + password + "'");

        try {
            while (res.next()) {
                String name = res.getString("name");
                String surname = res.getString("surname");
                Boolean admin = res.getBoolean("admin");
                user = new User(username, name, surname, admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static List<Teach> getTeaches() {
        List<Teach> teaches = new ArrayList<>();
        ResultSet res = executeQuery("SELECT teachers.id AS teacherId, teachers.name AS teacherName, " +
                "teachers.surname AS teacherSurname, courses.id AS courseId, " +
                "courses.name AS courseName, courses.description AS courseDescription " +
                "FROM (teaches " +
                "JOIN courses ON courses.id = teaches.courseId " +
                "JOIN teachers ON teachers.id = teaches.teacherId) " +
                "WHERE teaches.available = 1 " +
                "ORDER BY courses.name");

        try {
            while (res.next()) {
                int teacherId = res.getInt("teacherId");
                String teacherName = res.getString("teacherName");
                String teacherSurname = res.getString("teacherSurname");
                Teacher teacher = new Teacher(teacherId, teacherName, teacherSurname);

                int courseId = res.getInt("courseId");
                String courseName = res.getString("courseName");
                String courseDescription = res.getString("courseDescription");
                Course course = new Course(courseId, courseName, courseDescription);

                teaches.add(new Teach(teacher, course));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teaches;
    }

    public static boolean setTeach(int teacherId, int courseId) {
        boolean res = true;
        String t = "'" + teacherId + "', ";
        String c = "'" + courseId + "'";
        try {
            executeUpdate("INSERT INTO teaches (teacherId, courseId) " +
                    "VALUES (" + t + c + ");");
        } catch (SQLIntegrityConstraintViolationException e) {
            try {
                ResultSet rs = executeQuery("SELECT * FROM teaches " +
                        "WHERE teacherId = " + teacherId + " " +
                        "AND courseId = " + courseId + " " +
                        "AND available = false");
                if (rs.isBeforeFirst()) {
                    try {
                        executeUpdate("UPDATE teaches SET available = '1' " +
                                "WHERE teacherId = " + teacherId + " " +
                                "AND courseId = " + courseId);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        res = false;
                    }
                } else {
                    res = false;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                res = false;
            }
        } catch (SQLException e) {
            res = false;
            e.printStackTrace();
        }

        return res;
    }

    public static void deleteTeach(int teacherId, int courseId) {
        String t = "'" + teacherId + "'";
        String c = "'" + courseId + "'";

        try {
            executeUpdate("UPDATE teaches " +
                    "SET available = '0' " +
                    "WHERE courseId = " + c + " " +
                    "AND teacherId = " + t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<Booking> getBookings(String username) {
        List<Booking> bookings = new ArrayList<>();

        ResultSet res = executeQuery("SELECT bookings.id AS bookingId, teachers.id AS teacherId, " +
                "teachers.name AS teacherName, teachers.surname AS teacherSurname, " +
                "courses.id AS courseId, courses.name AS courseName, courses.description AS courseDescription, " +
                "users.username AS userUsername, users.name AS userName, users.surname as userSurname, " +
                "users.admin as userAdmin, bookings.day as bookingDay, bookings.hour AS bookingHour, " +
                "bookings.status AS bookingStatus " +
                "FROM (bookings " +
                "JOIN teaches ON bookings.teacherId = teaches.teacherId AND bookings.courseId = teaches.courseId " +
                "JOIN teachers ON teachers.id = teaches.teacherId " +
                "JOIN courses ON courses.id = teaches.courseId " +
                "JOIN users ON bookings.userId = users.username) " +
                "WHERE users.username = '" + username + "' " +
                "ORDER BY bookings.day, bookings.hour");

        try {
            while (res.next()) {
                int bookingId = res.getInt("bookingId");
                int teacherId = res.getInt("teacherId");
                String teacherName = res.getString("teacherName");
                String teacherSurname = res.getString("teacherSurname");
                Teacher teacher = new Teacher(teacherId, teacherName, teacherSurname);

                int courseId = res.getInt("courseId");
                String courseName = res.getString("courseName");
                String courseDescription = res.getString("courseDescription");
                Course course = new Course(courseId, courseName, courseDescription);

                String userUsername = res.getString("userUsername");
                String userName = res.getString("userName");
                String userSurname = res.getString("userSurname");
                Boolean userAdmin = res.getBoolean("userAdmin");
                User user = new User(userUsername, userName, userSurname, userAdmin);

                int bookingDay = res.getInt("bookingDay");
                String bookingHour = res.getString("bookingHour");
                String bookingStatus = res.getString("bookingStatus");

                bookings.add(new Booking(bookingId, teacher, course, user, bookingDay, bookingHour, bookingStatus));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return bookings;
    }

    public static List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();

        ResultSet res = executeQuery("SELECT bookings.id AS bookingId, teachers.id AS teacherId, " +
                "teachers.name AS teacherName, teachers.surname AS teacherSurname, " +
                "courses.id AS courseId, courses.name AS courseName, courses.description AS courseDescription, " +
                "users.username AS userUsername, users.name AS userName, users.surname as userSurname, " +
                "users.admin as userAdmin, bookings.day as bookingDay, bookings.hour AS bookingHour, " +
                "bookings.status AS bookingStatus " +
                "FROM (bookings " +
                "JOIN teaches ON bookings.teacherId = teaches.teacherId AND bookings.courseId = teaches.courseId " +
                "JOIN teachers ON teachers.id = teaches.teacherId " +
                "JOIN courses ON courses.id = teaches.courseId " +
                "JOIN users ON bookings.userId = users.username)");

        try {
            while (res.next()) {
                int bookingId = res.getInt("bookingId");
                int teacherId = res.getInt("teacherId");
                String teacherName = res.getString("teacherName");
                String teacherSurname = res.getString("teacherSurname");
                Teacher teacher = new Teacher(teacherId, teacherName, teacherSurname);

                int courseId = res.getInt("courseId");
                String courseName = res.getString("courseName");
                String courseDescription = res.getString("courseDescription");
                Course course = new Course(courseId, courseName, courseDescription);

                String userUsername = res.getString("userUsername");
                String userName = res.getString("userName");
                String userSurname = res.getString("userSurname");
                Boolean userAdmin = res.getBoolean("userAdmin");
                User user = new User(userUsername, userName, userSurname, userAdmin);

                int bookingDay = res.getInt("bookingDay");
                String bookingHour = res.getString("bookingHour");
                String bookingStatus = res.getString("bookingStatus");

                bookings.add(new Booking(bookingId, teacher, course, user, bookingDay, bookingHour, bookingStatus));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return bookings;
    }

    public static Boolean setBook(int teacherId, int courseId, String username, int day, String hour, String status) {
        boolean res;
        String t = "'" + teacherId + "', ";
        String c = "'" + courseId + "', ";
        String u = "'" + username + "', ";
        String d = "'" + day + "', ";
        String h = "'" + hour + "', ";
        String s = "'" + status + "'";
        if (!areBusy(teacherId, username, day, hour)) {// Utente o prof liberi
            System.out.println("Is not busy");
            int idIfCanceled = TutoringDAO.isCanceled(teacherId, courseId, username, day, hour);
            if (idIfCanceled > -1) {//se esiste ed è cancellata
                res = TutoringDAO.updateStatus(idIfCanceled, "booked");
            } else {//se non esiste
                try {
                    executeUpdate("INSERT INTO bookings (teacherId, courseId, userId, day, hour, status) " +
                            "VALUES (" + t + c + u + d + h + s + ");");
                    res = true;

                } catch (SQLException e) {
                    res = false;
                    e.printStackTrace();
                }
            }
        } else {//book e done
            System.out.println("Is busy");
            res = false;
        }


        return res;
    }

    public static void deleteBook(int bookId) {
        genericExecuteUpdate("DELETE FROM bookings " +
                "WHERE id = " + bookId);
    }

    public static int isCanceled(int teacherId, int courseId, String userId, int day, String hour) {
        int id = -1;
        ResultSet res = executeQuery("SELECT id FROM bookings " +
                "WHERE teacherId = '" + teacherId + "' AND " +
                "userId = '" + userId + "' AND " +
                "courseId = '" + courseId + "' AND " +
                "day = '" + day + "' AND " +
                "hour = '" + hour + "' AND " +
                "status = 'canceled'");
        try {
            if (res.next())
                id = res.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("id: " + id);
        return id;
    }

    public static Boolean areBusy(int teacherId, String userId, int day, String hour) {
        boolean isBusy;
        //TODO AGGIUSTARE QUESTA ED ANCHE IS BUSY

        /*
        ResultSet res = executeQuery("SELECT * FROM bookings WHERE " +
                "status != 'canceled' " +
                "AND day = '" + day + "' AND " +
                "hour = '" + hour + "' AND " +
                "(teacherId = '" + teacherId + "' OR " +
                "userId = '" + userId + "');");
*/


        ResultSet res = executeQuery("SELECT * FROM bookings WHERE " +
                "status != 'canceled' " +
                "AND " +
                "day = '" + day + "' AND " +
                "hour = '" + hour + "' AND " +
                "teacherId = '" + teacherId + "'");

        ResultSet res2 = executeQuery("SELECT * FROM bookings WHERE " +
                "status != 'canceled' " +
                "AND " +
                "day = '" + day + "' AND " +
                "hour = '" + hour + "' AND " +
                "userId = '" + userId + "'");
        try {
            int c = 0;
            while (res.next()) {
                c++;
            }
            while (res2.next()) {
                c++;
            }
            isBusy = c > 0;
        } catch (SQLException e) {
            isBusy = true;
            e.printStackTrace();
        }

        System.out.println("t: " + teacherId + " u: " + userId + " d: " + day + " h: " + hour);
        System.out.println("ARE BUSY E' " + isBusy);
        return isBusy;
    }

    public static Boolean isBusy(int teacherId, int day, String hour) {
        boolean isBusy = true;
        ResultSet res = executeQuery("SELECT * FROM bookings " +
                "WHERE teacherId = '" + teacherId + "' AND " +
                "day = '" + day + "' AND " +
                "hour = '" + hour + "' AND " +
                "(status = 'booked' OR status = 'done')");

        try {
            isBusy = res.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isBusy;
    }

    public static String getStatus(int id) {
        String status = "";
        ResultSet res = executeQuery("SELECT * FROM bookings " +
                "WHERE id = " + id);

        try {
            res.next();
            status = res.getString("status");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public static boolean updateStatus(int id, String status) {
        boolean res = true;
        try {
            executeUpdate("UPDATE bookings " +
                    "SET status = '" + status + "' " +
                    "WHERE id = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }

}
