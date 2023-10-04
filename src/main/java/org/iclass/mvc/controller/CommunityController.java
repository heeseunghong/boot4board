package org.iclass.mvc.controller;

import org.iclass.mvc.dto.Community;
import org.iclass.mvc.dto.CommunityComments;
import org.iclass.mvc.dto.PageRequestDTO;
import org.iclass.mvc.dto.PageResponseDTO;
import org.iclass.mvc.service.CommunityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Controller
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService service;

    private CommunityController(CommunityService service){
        this.service = service;
    }

   /* @GetMapping("/list")
    public void list(@RequestParam(defaultValue = "1") int page, Model model){
        model.addAttribute("list",service.pagelist(page).get("list"));
        model.addAttribute("paging",service.pagelist(page).get("paging"));
        model.addAttribute("today", LocalDate.now());
    }*/
    /*community list 와 검색 기능*/
    @GetMapping("/list")
    public void pagelist(PageRequestDTO pageRequestDTO, Model model){
        PageResponseDTO responseDTO =  service.listWithSearch(pageRequestDTO);
        //list.html 에 전달한 model 관련 코드 작성. list.html 도 완성하기. 레이아웃도 적용 하기
        model.addAttribute("list",service.pagelist(pageRequestDTO));
        model.addAttribute("paging",responseDTO);
        model.addAttribute("page",pageRequestDTO.getPage());
        model.addAttribute("today", LocalDate.now());
    }

    @GetMapping("/read")
    public void read(long idx, @ModelAttribute("page") int page, Model model, HttpServletRequest request){
        String remoteAddr = request.getRemoteAddr();
        model.addAttribute("remoteAddr", remoteAddr);
        model.addAttribute("vo", service.read(idx));
        model.addAttribute("cmtlist", service.commentsList(idx));
    }

    @GetMapping("/write")
    public void write(){

    }
    @PostMapping("/write")
    public String save(Community dto, RedirectAttributes redirectAttributes,HttpServletRequest request){
        service.insert(dto);

        String remoteAddr = request.getRemoteAddr();
        redirectAttributes.addAttribute("remoteAddr", remoteAddr);
        redirectAttributes.addFlashAttribute("message","글 등록이 완료되었습니다.");
        return "redirect:/community/list";
    }
    @PostMapping("/update")
    public void update(long idx, @ModelAttribute("page")int page, Model model){
        model.addAttribute("vo",service.selectByIdx(idx));
    }

    @PostMapping("/save")
    public String updateSave(int page, Community vo, RedirectAttributes redirectAttributes,HttpServletRequest request){
        service.update(vo);

        String remoteAddr = request.getRemoteAddr();
        redirectAttributes.addAttribute("remoteAddr", remoteAddr);
        redirectAttributes.addAttribute("idx",vo.getIdx());
        redirectAttributes.addAttribute("page",page);
        redirectAttributes.addFlashAttribute("message","글 수정이 완료되었습니다.");

        return "redirect:/community/read";
    }

    @PostMapping("/delete")
    public String delete(@ModelAttribute("page") int page, long idx, RedirectAttributes redirectAttributes){
        service.delete(idx);

        redirectAttributes.addAttribute("page",page);
        redirectAttributes.addFlashAttribute("message","글 삭제가 완료되었습니다.");

        return "redirect:/community/list";
    }

    @PostMapping("/comments")
    public String comments(int page, int f, CommunityComments dto,
                           RedirectAttributes redirectAttributes) {
        service.comments(dto, f);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("idx", dto.getMref());

        String message = null;
        if (f == 1) {
            message = "댓글 등록 하였습니다.";
        } else if (f == 2) {
            message = "댓글 삭제 하였습니다.";
        }
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/community/read";
    }


}
