package com.example.Musify.controller;

import com.example.Musify.model.Song;
import com.example.Musify.service.SongService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin("*")
public class SongController {

    @Autowired
    private SongService songService;
    
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @PostMapping("/upload")
    public ResponseEntity<Song> uploadSong(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist
    ) throws IOException {
        return ResponseEntity.ok(songService.uploadSong(file, title, artist));
    }

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(songService.searchSongs(search));
        }
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String fileId) {
        try {
            ObjectId objectId = new ObjectId(fileId);
            Query query = new Query(Criteria.where("_id").is(objectId));
            com.mongodb.client.gridfs.model.GridFSFile gridFSFile = gridFsTemplate.findOne(query);
            
            if (gridFSFile == null) {
                return ResponseEntity.notFound().build();
            }
            
            GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
            if (resource.exists()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(resource.getContentType()));
                headers.setContentLength(resource.contentLength());
                headers.set("Content-Disposition", "inline; filename=\"" + resource.getFilename() + "\"");
                
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new InputStreamResource(resource.getInputStream()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
