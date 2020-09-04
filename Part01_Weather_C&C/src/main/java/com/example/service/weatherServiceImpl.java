package com.example.service;

import java.io.IOException;

import java.util.List;

import javax.inject.Inject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.Util.DateLoader;
import com.example.dao.FcastWeatherJSON;
import com.example.dao.NowWeatherJSON;
import com.example.dao.NowWeatherJSON_SKY;
import com.example.dao.weatherDAO;
import com.example.dto.DistrictVO;
import com.example.dto.FcastWeatherVO;
import com.example.dto.NowWeatherVO;

@Service
public class weatherServiceImpl implements weatherService {

	private static final Logger logger = LoggerFactory.getLogger(weatherServiceImpl.class);

	@Inject
	private weatherDAO dao;

	@Override
	public List<DistrictVO> selectarea() throws Exception {
		return dao.selectarea();
	}

	@Override
	public List<FcastWeatherVO> selectfw(String nowday, List<DistrictVO> areaList, String areaID) throws Exception {
		// 예보날씨 가져오기

		String District_name_step1 = null;

		for (int i = 0; i < areaList.size(); i++) {

			if (areaID.equals(areaList.get(i).getDistrict_ID())) {
				District_name_step1 = areaList.get(i).getDistrict_name_step1();
			}

		}
		logger.info("예보지역명/" + District_name_step1);

		// 마지막 3일 구하기
		String[] fcDayList = DateLoader.fcDay(nowday);

		String day1 = fcDayList[0];
		String day3 = fcDayList[2];

		logger.info("예보 1일" + day1);
		logger.info("예보 3일" + day3);

		FcastWeatherVO fwVO_S = new FcastWeatherVO(areaID, day1, day3);
		logger.info("fwVO_S 확인" + fwVO_S.day1 + "/" + fwVO_S.day3 + "/" + fwVO_S.district_ID);

		return dao.selectfw(fwVO_S);
	}

	@Override
	public List<NowWeatherVO> selectnw(String now) {

		String nowDate = now.substring(0, 8);
		String nowTime = now.substring(8);

		NowWeatherVO nwVO_S = new NowWeatherVO(nowDate, nowTime);
		
		logger.debug("nwVO_S 확인" + nwVO_S.nowDate + "//" + nwVO_S.nowTime);

		return dao.selectnw(nwVO_S);
	}

	@Override
	public void insertnw(String now, List<DistrictVO> areaList) throws IOException, ParseException {
		// 현재날씨 요쳥하기
		logger.debug("@@@@@@@@@@@@@ 현재 날씨 API 요청 시작 @@@@@@@@@@@@@");

		String now_day = now.substring(0, 8);
		String now_time = now.substring(8);
		logger.debug("현재날자 시간확인"+now_day+"//"+now_time);

		for (int i = 0; i < areaList.size(); i++) {
			
			String district_ID = areaList.get(i).getDistrict_ID();

			logger.debug(areaList.get(i).getDistrict_ID() + " 지역 API요청 시작");

			int nx = areaList.get(i).getNX();
			logger.debug("****** 지역 nx = " + nx);

			int ny = areaList.get(i).getNY();
			logger.debug("****** 지역 ny = " + ny);

			// 기상 데이터를 얻어오는 객체 생성
			NowWeatherJSON nwJson = new NowWeatherJSON();
			NowWeatherJSON_SKY nwJson_SKY = new NowWeatherJSON_SKY();

			NowWeatherVO nw = new NowWeatherVO();

			nw = nwJson.getNowWeather(now_day, now_time, nx, ny); // 초단기 실황
			logger.debug("1) NowWeatherVO에 초단기 실황 저장완료");
			nw.SKY = nwJson_SKY.getSKY(now_day, now_time, nx, ny); // 초단기 예보
			logger.debug("2) NowWeatherVO에 초단기예보 저장완료");

			NowWeatherVO nwVO_S = new NowWeatherVO(district_ID, nw.getNowDate(), nw.getNowTime(), nw.getSKY(), nw.getNTH());
			
			logger.debug("nwVO_S 확인" +"//"+ nwVO_S.getDistrict_ID() +"//"+ nwVO_S.getNowDate() +"//"+ nwVO_S.getNowTime() +"//"+ nwVO_S.getSKY() +"//"+ nwVO_S.getNTH());

			logger.debug("3) insert 시작");
			dao.insertnw(nwVO_S);

		}

	}

	@Override
	public void insertfw(String now, List<DistrictVO> areaList) throws IOException, ParseException {
		// 예보날씨 요청하기
		String now_day = now.substring(0, 8);
		String now_time = now.substring(8);
		
		String areaname = null;
		
		for (int i = 0; i < areaList.size(); i++) {
			logger.info("============================================================================");
			areaname = areaList.get(i).getDistrict_ID();
			logger.debug(areaname + " 지역 예보날씨 API요청 시작");
			int nx = areaList.get(i).getNX();
			int ny = areaList.get(i).getNY();
			logger.debug("****** 지역 nx = " + nx+ "/지역 ny = " + ny);

			// 현재시간 계산하기
			int nowtime = Integer.parseInt(now_time); 
			int nowday = Integer.parseInt(now_day); 
			logger.debug("날짜/" + now_day + "시간/" + now_time);
			
			String districtID = areaList.get(i).getDistrict_ID();

			// 기상 데이터를 얻어오는 객체 생성
			FcastWeatherJSON fcJson = new FcastWeatherJSON();

			logger.info("1) 예보날씨 API 요청 시작");
			List<FcastWeatherVO>fwList = fcJson.getFcastWeather(areaname,districtID, nowday, nowtime, nx, ny);
			
			for (int a = 0; a<fwList.size(); a++) {
				logger.info("fwList 예보 리스트 확인");
				logger.debug(fwList.get(a).getDistrict_ID()+"/"+fwList.get(a).getFcastDate()+"/"+fwList.get(a).getFcastTime()+"/"+fwList.get(a).getSKY()+"/"+fwList.get(a).getFTH());
			}
			
			logger.debug("2) insert 시작");
			dao.insertfw(fwList);
			logger.debug("3) 에보날씨저장완료 !!!!!"+"현재날짜"+nowday+"현재시간 ="+nowtime);
		
		}
		
	}

	@Override
	public void NWjsonObject_view(Model model, List<NowWeatherVO> nwList,List<DistrictVO> areaList) {
		JSONObject NWjsonObject = new JSONObject();// 최종 완성될 JSonObject

		// 지역정보입력
		for (int i = 0; i < nwList.size(); i++) {

			String areaname = null;
			double X;
			double Y;

			int num = nwList.size();

			for (int a = 0; a < areaList.size(); a++) {

				if (nwList.get(i).getDistrict_ID().equals(areaList.get(a).getDistrict_ID())) {
					areaname = areaList.get(a).getDistrict_name_step1();
					X = areaList.get(a).getX();
					Y = areaList.get(a).getY();

					logger.info("지역명/" + areaname);
					logger.info("===========================" + areaname + "===========================");

					JSONArray areaArray = new JSONArray(); // 지역이름 json정보 array
					JSONObject areaNWInfo = new JSONObject(); // 지역정보및 현재날씨 정보가 들어갈 Jsonobject

					areaNWInfo.put("District_ID", nwList.get(i).getDistrict_ID());
					areaNWInfo.put("district_name_step1", areaname);
					areaNWInfo.put("X", X);
					areaNWInfo.put("Y", Y);
					areaNWInfo.put("NTH", nwList.get(i).getNTH());
					areaNWInfo.put("SKY", nwList.get(i).getSKY());

					logger.debug("확인 areaArray리스트 들어가기전에 확인 ==== " + areaNWInfo);
					areaArray.add(areaNWInfo);
					logger.debug("확인 NWjsonObject 들어가기전에 확인 ==== " + areaArray);

					NWjsonObject.put(areaname, areaArray);
					logger.debug("NWjsonObject에  잘 들어갔는지 확인 ==== " + areaArray);

				}

			}

		}

		String nw = NWjsonObject.toJSONString();
		logger.debug("자바스크립트 보낼꺼 확인" + nw);
		model.addAttribute("nwList", nw);
		
	}

	@Override
	public String FWjsonObject_view(List<FcastWeatherVO> fcDtos, List<DistrictVO> areaList, String now) {
		// 날짜 배열 가져오기
				String[] fcDayList = DateLoader.fcDay(now);
				String fcDay = null;

				JSONObject FCObject = new JSONObject();// 최종 완성될 JSonObject
				JSONArray areaArray = new JSONArray(); // 지역이름 json정보 array

				String areaname = null;

				// 지역정보입력
				for (int i = 0; i < fcDtos.size(); i++) {

					for (int a = 0; a < areaList.size(); a++) {

						if (fcDtos.get(i).getDistrict_ID().equals(areaList.get(a).getDistrict_ID())) {
							areaname = areaList.get(a).getDistrict_name_step1();

							logger.info("지역명/" + areaname);
							logger.info("===========================" + areaname + "===========================");

							String FcastDate = fcDtos.get(i).getFcastDate();

							JSONObject areaFWInfo = new JSONObject(); // 지역정보및 현재날씨 정보가 들어갈 Jsonobject

							if (fcDtos.get(i).getFcastTime().equals("0000")) {
								areaArray = new JSONArray(); // 지역이름 json정보 array
							}
							areaFWInfo.put("fcTime", fcDtos.get(i).getFcastTime());
							areaFWInfo.put("fcDate", fcDtos.get(i).getFcastDate());
							areaFWInfo.put("district_name_step1", areaname);
							areaFWInfo.put("FTH", fcDtos.get(i).getFTH());
							logger.info("확인 areaArray리스트 들어가기전에 확인 ==== " + areaFWInfo);
							areaArray.add(areaFWInfo);
							logger.info("확인 FWjsonObject 들어가기전에 확인 ==== " + areaArray);

							FCObject.put(FcastDate, areaArray);
							logger.info("FWjsonObject에  잘 들어갔는지 확인 ==== " + areaArray);
						}

					}
				}

				String Sfw = FCObject.toJSONString();
				return Sfw;
	}

}
