package com.example.service;

import java.io.IOException;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.ui.Model;

import com.example.dto.DistrictVO;
import com.example.dto.FcastWeatherVO;
import com.example.dto.NowWeatherVO;

public interface weatherService {

	public List<DistrictVO> selectarea() throws Exception;

	public List<FcastWeatherVO> selectfw(String nowday, List<DistrictVO> areaList, String areaID) throws Exception;

	public List<NowWeatherVO> selectnw(String now);

	public void insertnw(String nwnow, List<DistrictVO> areaList) throws IOException, ParseException;

	public void insertfw(String now, List<DistrictVO> areaList) throws IOException, ParseException;

	public void NWjsonObject_view(Model model, List<NowWeatherVO> nwList, List<DistrictVO> areaList);

	public String FWjsonObject_view(List<FcastWeatherVO> fcDtos, List<DistrictVO> areaList, String now);

}
