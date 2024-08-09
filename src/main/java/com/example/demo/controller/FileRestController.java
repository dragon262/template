package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/files")
@RestController
public class FileRestController {
	
	@Value("${pvc.path}")
	private String pvcPath;

    public FileRestController() {
    }
    
    /**
     * FileItem 목록 조회
     *
     * @param pageable
     * @param id
     * @param keyword
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getFileItemList(@PathVariable(name = "id", required = false) Long id, @PathVariable(name = "keyword", required = false) String keyword) {

		try {
			//
			File directory = new File(pvcPath);
			File[] files = directory.listFiles();
			List.of(files);
			return ResponseEntity.ok(List.of(files).stream().map(f -> Map.of(
					"name", f.getName(),
					"path",	f.getAbsolutePath())) /* todoService.getTodoList(pageable) */);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
	@PostMapping
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
		
		// 저장할 파일 이름 중간에 "_"를 이용해서 구현
        String saveName = pvcPath + File.separator + File.separator + file.getOriginalFilename();

        Path savePath = Paths.get(saveName);

        try {
        	file.transferTo(savePath); // 실제 이미지 저장
        } catch (IOException e) {
        	return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

		return ResponseEntity.ok().body("success");
	}
    
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable("fileName") String fileName) throws MalformedURLException {

        UrlResource urlResource = new UrlResource("file:" + pvcPath + File.separatorChar + fileName);

        String encodeUploadFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodeUploadFileName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }
    
    
}
