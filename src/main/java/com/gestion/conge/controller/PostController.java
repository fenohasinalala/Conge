package com.gestion.conge.controller;

import com.gestion.conge.model.Post;
import com.gestion.conge.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class PostController {
    private PostService postService;

    @GetMapping("/hello")
    public List<String> hello(){
        return List.of("Hello","World");
    }

    @GetMapping("/posts")
    public List<Post> getPosts(@RequestParam int page,
                               @RequestParam(value = "page_size") int pageSize,
                               @RequestParam(value = "name", required = false, defaultValue = "") String name){
        return postService.getPosts(page, pageSize, name);
    }

    @PostMapping("/posts")
    public Post addPost(@RequestBody Post post){
        return postService.addPost(post);
    }

    @GetMapping("/posts/{id}")
    public Post getPostById(@PathVariable Long id) throws Exception {
        return postService.getPostById(id);
    }

    @DeleteMapping ("/posts/{id}")
    public Post deletePostById(@PathVariable Long id) throws Exception {
        return postService.deletePostById(id);
    }

    @PutMapping ("/posts/{id}")
    public Post modifyPostById(@PathVariable Long id, @RequestBody Post newPost) throws Exception {
        return postService.modifyPostById(id, newPost);
    }
}
