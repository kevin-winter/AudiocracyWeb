/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.fhjoanneum.swenga.project.audiocracyweb.servlets;

import at.fhjoanneum.swenga.project.audiocracyweb.controller.LocationController;
import at.fhjoanneum.swenga.project.audiocracyweb.entities.Image;
import at.fhjoanneum.swenga.project.audiocracyweb.facades.LocationFacade;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Kevin
 */
@WebServlet(name = "ImagesServlet", urlPatterns = {"/showImage", "/showThumbnail"})
public class ImageServlet extends HttpServlet {
    @Inject
    private LocationController controller;    
    @Inject
    private LocationFacade pf;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long imgIndex;
        String name;
        Image img = null;
        try {

            try {
                imgIndex = Long.parseLong(request.getParameter("index"));
                img = pf.findImageById(imgIndex);
            } catch (NumberFormatException | NullPointerException ex) {
                name = request.getParameter("name");
                for (Image image : controller.getSelected().getImages()) {
                    if (image.getName().equals(name)) {
                        img = image;
                    }
                }
            }
            response.setContentType(img.getType());
            response.setHeader("Content-Disposition", "filename=\"" + img.getName() + "\"");
            ServletOutputStream out = response.getOutputStream();
            out.write(request.getRequestURI().endsWith("Thumbnail") ? img.getThumbnail() : img.getContent());
            out.close();
        } catch (Exception exception) {
        }

    }
}
