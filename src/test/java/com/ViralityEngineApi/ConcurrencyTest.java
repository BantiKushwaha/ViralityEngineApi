package com.ViralityEngineApi;

import com.ViralityEngineApi.dto.CommentDto;
import com.ViralityEngineApi.dto.PostDto;
import com.ViralityEngineApi.entities.Post;
import com.ViralityEngineApi.services.CommentService;
import com.ViralityEngineApi.services.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Test
    public void testBotReplyHorizontalCap() throws InterruptedException {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setAuthorType("human");
        postDto.setContent("Test post for concurrency");
        
        Post post = postService.createPost(postDto);
        Long postId = post.getPostId();

        int concurrentRequests = 200;
        int expectedSuccessful = 100;
        int expectedFailed = concurrentRequests - expectedSuccessful;

        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < concurrentRequests; i++) {
            final int botId = i + 1;
            executor.submit(() -> {
                try {
                    CommentDto commentDto = new CommentDto();
                    commentDto.setPostId(postId);
                    commentDto.setAuthorId((long) botId);
                    commentDto.setAuthorType("bot");
                    commentDto.setContent("Bot comment " + botId);
                    commentDto.setDepthLevel(1);

                    commentService.addComment(commentDto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(expectedSuccessful, successCount.get(), 
            "Should allow exactly 100 bot comments");
        assertEquals(expectedFailed, failureCount.get(), 
            "Should reject " + expectedFailed + " requests due to horizontal cap");

        System.out.println("Concurrency Test Results:");
        System.out.println("Successful comments: " + successCount.get());
        System.out.println("Failed comments: " + failureCount.get());
        System.out.println("Total requests: " + concurrentRequests);
    }

    @Test
    public void testCommentDepthVerticalCap() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setAuthorType("human");
        postDto.setContent("Test post for depth limit");
        
        Post post = postService.createPost(postDto);
        Long postId = post.getPostId();

        CommentDto commentDto = new CommentDto();
        commentDto.setPostId(postId);
        commentDto.setAuthorId(1L);
        commentDto.setAuthorType("bot");
        commentDto.setContent("Deep comment");
        commentDto.setDepthLevel(21);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.addComment(commentDto);
        });

        assertTrue(exception.getMessage().contains("Comment thread depth exceeded"));
    }
}
