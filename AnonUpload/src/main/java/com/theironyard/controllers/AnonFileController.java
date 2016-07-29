package com.theironyard.controllers;

import com.theironyard.entities.AnonFile;
import com.theironyard.entities.PasswordStorage;
import com.theironyard.services.AnonFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by jeff on 7/27/16.
 */
@RestController
public class AnonFileController {

    @Autowired
    AnonFileRepository repository;

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public void upload(MultipartFile file, String noDelete, String comment, String password, HttpServletResponse response) throws IOException, PasswordStorage.CannotPerformOperationException {
        File dir = new File("public/files");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        //Delete files, from database, if there are more than max allowed (max).
        long max = 5;
        while (repository.count() >= max) {
            AnonFile deleteFile = repository.findFirstByPermanentFalseOrderByIdAsc();
            repository.delete(deleteFile.getId());
        }

        File f = File.createTempFile("anonfile", file.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(file.getBytes());

        AnonFile anonFile = new AnonFile(f.getName(), file.getOriginalFilename());

        //Did the user enter a comment?
        if (!comment.isEmpty()){
            anonFile.setComment(comment);
        }

        //Do we want to make the file permanent?
        if (noDelete == null){
            anonFile.setPermanent(false);
        }else{
            anonFile.setPermanent(true);
        }

        //Did user use a password?
        if (!password.isEmpty()){
            anonFile.setPassword(PasswordStorage.createHash(password));
        }
        repository.save(anonFile);
        response.sendRedirect("/");
    }

    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public void delete(String deleteFile, HttpServletResponse response, String password) throws IOException, PasswordStorage.InvalidHashException, PasswordStorage.CannotPerformOperationException {

        AnonFile file = repository.findByOriginalFilename(deleteFile);
        if (file.getPassword() == null){
            repository.delete(file.getId());
        }else if (PasswordStorage.verifyPassword(password, file.getPassword())){
            repository.delete(file.getId());
        }

        response.sendRedirect("/");
    }

    @RequestMapping(path = "/files", method = RequestMethod.GET)
    public List<AnonFile> getFiles(){
        return repository.findAll();
    }
}