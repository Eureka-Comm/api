package com.castellanos94.fuzzylogic.api.controller;

import com.castellanos94.fuzzylogic.api.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
public class FileUploadController {

	@RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public void upload(@RequestPart("user") @Valid User user,
			@RequestPart("file") @Valid @NotNull @NotBlank MultipartFile file) {
		System.out.println(user);
		printFileDetails(file);
	}

	@RequestMapping(value = "/api/endpoint", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public void upload(@RequestPart("file") @Valid @NotNull @NotBlank MultipartFile file) {
		printFileDetails(file);
	}

	public static void printFileDetails(MultipartFile file) {
		System.out.println("Uploaded File: ");
		System.out.println("Name : " + file.getName());
		System.out.println("Type : " + file.getContentType());
		System.out.println("Name : " + file.getOriginalFilename());
		System.out.println("Size : " + file.getSize());
	}
}
