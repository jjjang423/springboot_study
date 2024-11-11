package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.service.CommentService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentService commentService; // 서비스 객체 주입

    @GetMapping("/articles/new")                                // 098p
    public String newArticleForm(){
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticle(ArticleForm form){
        log.info(form.toString());
        // System.out.println(form.toString());

        //  1. DTO를 엔티티로 변환
        Article article = form.toEntity();

        log.info(article.toString());
        // System.out.println(article.toString());

        //  2. 리파지터리로 엔티티를 DB에 저장
        Article saved = articleRepository.save(article);
        log.info(saved.toString());
        // System.out.println(saved.toString());

        return "redirect:/articles/" + saved.getId();
    }
    
    // GET
    @GetMapping("/articles/{id}")
    public  String show(@PathVariable Long id, Model model){
        log.info("id = " + id);
        Article articleEntity = articleRepository.findById(id).orElse(null);
        List<CommentDto> commentsDtos = commentService.comments(id);
        model.addAttribute("article",articleEntity);
        model.addAttribute("commentDtos", commentsDtos); // 댓글 목록 모델에 등록
        return "articles/show";
    }
    @GetMapping("/articles")
    public String index(Model model){
        List<Article> articleEntityList = articleRepository.findAll();
        model.addAttribute("articleList", articleEntityList);
        return "articles/index";
    }

    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model){
        Article articleEntity = articleRepository.findById(id).orElse(null);
        model.addAttribute("article", articleEntity);
        return "articles/edit";
    }

    @PostMapping("/articles/update")
    public String update(ArticleForm form){
        log.info(form.toString());
        Article articleEntity = form.toEntity();
        log.info(articleEntity.toString());
        Article target = articleRepository.findById(articleEntity.getId()).orElse(null);
        if(target != null){
            articleRepository.save(articleEntity);
        }
        return "redirect:/articles/" + articleEntity.getId();
    }

    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes rttr){
        log.info("삭제 요청이 들어왔습니다!!");
        Article target = articleRepository.findById(id).orElse(null);
        log.info(target.toString());
        if(target != null){
            articleRepository.delete(target);
            rttr.addFlashAttribute("msg", "삭제됐습니다!");
        }
        return "redirect:/articles";
    }
}