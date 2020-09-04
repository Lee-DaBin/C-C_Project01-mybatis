package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.Util.DateLoader;
import com.example.dao.weatherDAOImpl;
import com.example.dto.DistrictVO;
import com.example.dto.FcastWeatherVO;
import com.example.dto.NowWeatherVO;
import com.example.service.MainpageSErviceImpl;
import com.example.service.MainpageService;
import com.example.service.weatherService;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Inject
	private weatherService service;
	private MainpageService mainService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) throws Exception {

		logger.info("home");
		logger.info("Welcome home! The client locale is {}.", locale);

		List<DistrictVO> areaList = service.selectarea(); // 지역테이블 
		String now = new DateLoader().DateLader(); // 현재 날짜 시간 
		logger.info("현재시간"+now);
		
		
		// 메인화면 구성
		logger.info("=== 1) 메인화면 구성");
		mainService = new MainpageSErviceImpl();
		mainService.execute(model, now, areaList);

		// 현재날씨 요청 및 출력
		logger.info("=== 2) 현재날씨 시작");
		service.insertnw(now, areaList);
		List<NowWeatherVO> nwList = service.selectnw(now);
		service.NWjsonObject_view(model,nwList,areaList);
		
		
		// 예보날씨 요청 및 출력
		logger.info("=== 3) 예보날씨 시작");
		String areaID = areaList.get(0).getDistrict_ID(); // 디폴트 지역 (서울)
		String nowday = now.substring(0, 8);
		logger.debug("오늘날짜 확인" + nowday+"디폴트 서울지역 areaID 확인 ---> " + areaID);
		List<FcastWeatherVO> fcDtos = service.selectfw(nowday, areaList, areaID); // 지역테이블 준비
		model.addAttribute("fcDtos", fcDtos);

		return "home";
	}

	// 선택한 지역 예보날씨 ajax
	@RequestMapping(value = "/fcWeatherList", method = RequestMethod.POST, produces = "application/text; charset=utf8") // 한글인코딩해서넘기기
	public @ResponseBody String ajax_fcWeatherbtn(@RequestBody String ID, Model model) throws Exception {
		
		List<DistrictVO> areaList = service.selectarea(); // 지역테이블 준비
		String now = new DateLoader().DateLader();
		String nowday = now.substring(0, 8);
		String areaID = ID.replaceAll("[^0-9]", ""); // 숫자만가져오기

		// 선택한 지역이름,예보날씨 보내기
		List<FcastWeatherVO> fcDtos = service.selectfw(nowday, areaList, areaID); // 지역테이블 준비
		String Sfw 	= service.FWjsonObject_view(fcDtos,areaList,now);
		//logger.debug("자바스크립트 보낼꺼 확인" + Sfw);
		return Sfw;
	}

}
