package org.iclass.mvc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iclass.mvc.dao.CommunityCommentsMapper;
import org.iclass.mvc.dao.CommunityMapper;
import org.iclass.mvc.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {
	//컨트롤러는 요청에 대한 view 를 담당. 비지니스 로직은 서비스에서 처리하도록 합니다.(개발자의 주요 처리내용)
	//서비스는 특정(단위) 기능을 중심을 메소드를 정의. dao는 하나의 sql로 만들어지는 메소드.
	private final CommunityMapper dao;
	private final CommunityCommentsMapper cmtdao;
	
	public List<Community> pagelist(PageRequestDTO dto){

		dto.setSize(10);			//한 페이지에 보이는 글의 갯수 설정
		dto.setDatas();				//start 와 end 계산
		List<Community> list = dao.pagelist(dto);		//글 목록

		//페이지 목록 구현은 예정.
		return list;
	}

	//페이지 목록과 글목록을 저장하는 DTO를 리턴타입으로 합니다.
	public PageResponseDTO listWithSearch(PageRequestDTO dto){
		dto.setSize(10);
		dto.setDatas();
		List<Community> list = dao.pagelist(dto);
		PageResponseDTO pageResponseDTO = PageResponseDTO.of(dto,dao.count(dto),5);
		pageResponseDTO.setList(list);

		return pageResponseDTO;
	}
	/* public Map<String,Object> pagelist(PageRequestDTO dto){
		//만들어진 페이지리스트와 Paging 정보를 같이 리턴하기 위해
		//List<Community> 에서 Map으로 변경함.

		int pageSize=10;		//pageSize 를 15 또는 10으로 변경해서 실행해 봅시다.
		int totalCount = dao.count();

		//위의 값들을 이용해서 Paging 객체를 생성하면서 다른 필드값을 계산합니다.
		Paging paging = new Paging(page, totalCount, pageSize);

		//pagelist() 메소드를 실행하기 위한 Map을 생성합니다.
		Map<String,Integer> map = new HashMap<>();
		map.put("start",paging.getStartNo());
		map.put("end",paging.getEndNo());

		List<Community> list = dao.pagelist(map);

		Map<String,Object> result = new HashMap<>();
		result.put("paging", paging);
		result.put("list", list);

		return result;
	} */

	//글 읽기
	public Community read(long idx) {
		dao.setReadCount(idx);		//조회수 증가
		return dao.selectByIdx(idx);	//PK로 조회
		
	}
	
	//글 수정
	public Community selectByIdx(long idx) {
		return dao.selectByIdx(idx);
	}
	//글 쓰기
	public int insert(Community vo) {
		return dao.insert(vo);
	}

	
	public int delete(long idx) {	//Integer, Long : 래퍼(wrapper) 클래스
		return dao.delete(idx);
	}

	public int update(Community dto) {
		return dao.update(dto);
	}

	@Transactional		//트랜잭션 처리를 위한 애노테이션 - 2개의 SQL이 하나의 처리 단위
	public void comments(CommunityComments dto, int f) {
		if(f==1) {			//댓글 등록
			cmtdao.insert(dto);
			cmtdao.setCommentCount(dto.getMref());		//댓글 개수 업데이트
		}else if(f==2) {	//댓글 삭제
			cmtdao.delete(dto.getIdx());
			cmtdao.setCommentCount(dto.getMref());		//댓글 개수 업데이트
		}
	}
	public List<CommunityComments> commentsList(long idx){	//댓글 목록
		return cmtdao.commentsList(idx);
	}

}
