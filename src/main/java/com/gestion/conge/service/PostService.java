package com.gestion.conge.service;

import com.gestion.conge.exception.BadRequestException;
import com.gestion.conge.exception.ResourceNotFoundException;
import com.gestion.conge.model.Post;
import com.gestion.conge.model.validation.PostValidator;
import com.gestion.conge.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;
    private PostValidator postValidator;

    public List<Post> getPosts(int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page - 1,pageSize,
                Sort.by(ASC,"name"));
        return postRepository.findByNameContainingIgnoreCase(name,pageable);
    }

    public Post addPost(Post post) {
        postValidator.accept(post);
        Optional<Post> postOptional = postRepository.findPostByName(post.getName());
        if (postOptional.isPresent()){
            throw new BadRequestException("Post with the same name already exist");
        }
        return postRepository.save(post);
    }

    public Post getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Post with id "+id +" does not exists"));
        return post;
    }

    public Post deletePostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Post with id "+id +" does not exists"));
        postRepository.deleteById(id);
        return post;
    }

    @Transactional
    public Post modifyPostById(Long id, Post newPost) {
        postValidator.accept(newPost);
        Post post = postRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Post with id "+id +" does not exists"));
        if(newPost.getName()!=null &&
                newPost.getName().length()>0 &&
                !Objects.equals(newPost.getName(),post.getName())){
            Optional<Post> postOptional = postRepository.findPostByName(newPost.getName());
            if (postOptional.isPresent()){
                throw new BadRequestException("Post with the same name already exist");
            }

            post.setName(newPost.getName());
        }
        return post;
    }
}
