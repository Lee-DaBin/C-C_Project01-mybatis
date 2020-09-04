<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!-- 한글깨질때 사용코드 -->
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page session="false"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<title>Home</title>

<haad>
<link rel="stylesheet"  href="resources/style/fctable.css"/>
</head>
<body>
	<div id="Box">
		
		<div id="head1" style="padding-left: 30px; padding-top: 30px;">
			<img alt="로고 사진" src='<spring:url value="/resources/img/logo.png"></spring:url>' id="logo" >
		</div>
		
		<div id="head2">
			<b id="maindate">${y}년 ${M}월 ${d}일 ${w}요일 <b id="mainAreaName">${mainAreaName}</b>기상예보</b>

		</div>

		<div id="body">

				<table id="bodytable"> <!-- 카카오 지도 테이블 -->
					<tr>
						<!-- 카카오지도 -->
						<td id="map" width="40%" ></td>
						
						<!-- 예보날씨 테이블 -->
						<td style="width: 60%">

							<table id="fcWeatehr" >
							
								<tr>
									<td id="DateTD" colspan="2" ><b>오늘 </b><small><b>${d}일</b></small></td>
								</tr>

								<tr id="fcWeather_DAY_tr1">
									<c:forEach items="${fcDtos}" var="fcList" end="7">

										<c:set var="nowtime" value="${Integer.parseInt(nowTime)}" />
										<c:set var="fctime"
											value="${Integer.parseInt(fcList.fcastTime)}" />
										<c:set var="T3H" value="${THList}" />

										<c:choose>
											<c:when test="${nowtime > fctime}">
												<c:set var="fctime" value="${fcList.fcastTime.substring(0,2)}" />
												<td id ="DataTD_now" style="">${fctime}시<br>${fcList.FTH.substring(0,2)}℃</td>
											</c:when>

											<c:otherwise>
												<c:set var="fctime" value="${fcList.fcastTime.substring(0,2)}" />
												<td id ="DataTD">${fctime}시<br>${fcList.FTH.substring(0,2)}℃</td>
											</c:otherwise>

										</c:choose>
									</c:forEach>
								</tr>

								<tr>
									<td id="DateTD" colspan="2" align="center"><b>내일 </b><small> <b>${d2}일</b></small></td>
								</tr>

								<tr id="fcWeather_DAY_tr2">
									<c:forEach items="${fcDtos}" var="fcList" begin="8" end="15">
										<td id ="DataTD">${fcList.fcastTime.substring(0, 2)}시<br>${fcList.FTH.substring(0,2)}℃</td>
									</c:forEach>
								</tr>

								<tr>
									<td id="DateTD" colspan="2" ><b>모레 </b><small><b> ${d3}일</b></small></td>
								</tr>

								<tr id="fcWeather_DAY_tr3">
									<c:forEach items="${requestScope.fcDtos}" var="fcList"
										begin="16" end="23">
										<td id ="DataTD">${fcList.fcastTime.substring(0, 2)}시<br>${fcList.FTH.substring(0,2)}℃</td>
									</c:forEach>
								</tr>

							</table>
						</td>
					</tr>
				</table>
		</div>
	</div>
</body>

<!-- 카카오지도 -->
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=6d476c3311dc5022627d4e6bf2604d8f&libraries=services"></script> 
							<script>
								var mapContainer = document.getElementById('map'), // 지도를 표시할 div
									mapOption = {
										center : new kakao.maps.LatLng(37,127), // 지도의 중심좌표
										level : 13
								};
								var map = new kakao.maps.Map(mapContainer,mapOption); // 지도를 생성합니다
								var nwList = JSON.parse('${nwList}');
								console.log(nwList);

								console.log(JSON.stringify(nwList));
								console.log("1) " + Object.keys(nwList));

								var keyList = Object.keys(nwList);

								for ( var i in keyList) {console.log("2) " + keyList[i] + ",  data="+ nwList[keyList[i]]);

									var obj = nwList[keyList[i]];

									for ( var key in obj) {
										//지역 선택
										console.log("3) " + "현재날씨리스트순서: " + key+ ", value: " + obj[key]);
										
										var obj2 = obj[key];

										// 현재날시에 나타낼 정보 변수  
										var d_ID;
										var d_name;
										var NTH;
										var SKY;
										var X;
										var Y;

										for ( var area in obj2) {
											console.log("4) " + "현재날씨 내용 목록: "+ area + ", value: "+ obj2[area]);

											if (area == 'district_name_step1') {
												d_name = obj2[area];
											} else if (area == 'District_ID') {
												d_ID = obj2[area];
											} else if (area == 'NTH') {
												NTH = obj2[area];
											} else if (area == 'SKY') {
												SKY = obj2[area];
											} else if (area == 'X') {
												X = obj2[area];
											} else if (area == 'Y') {
												Y = obj2[area];
											}
											;
										}
										
										//현재날씨 지역버튼생성하기
										console.log("지역 아이디/" + d_ID + "지역이름/"+ d_name + "하늘상태/" + SKY+ "온도/" + NTH + "X/" + X + "Y/"+ Y);

										// SKY 하늘상태 이미지화 
										console.log("하늘상태 확인 = " + SKY);

										if (SKY == 1) {
											SKY = "1.gif";
										} else if (SKY == 2) {
											SKY = "2.png";
										} else if (SKY == 3) {
											SKY = "3.png";
										} else {
											SKY = "4.png";
										}
										;

										console.log("하늘상태 이미지 변환 주소확인 = "+ SKY);

										// 버튼생성 HTML 
										content = '<button id="btn" value= "'
										content += d_ID+'">'+ d_name+ '<br><b style="padding-top:20px;">';
										content += NTH+ '℃</b>   <img src="resources/img/'+SKY+'"';
										content += 'alt="날씨사진" id ="Nwimg"> </button>';

										console.log("버튼 HTML 확인=" + content);
										//현재날씨 지역 버튼 생성 
										overlay = new kakao.maps.CustomOverlay(
												{
													content : content,
													map : map,
													position : new kakao.maps.LatLng(X, Y),
													yAnchor : 1.1
												});
									}
								}
							</script>

<!-- ajax 예보날씨 지역 버튼  -->
<script src="http://code.jquery.com/jquery-3.4.1.min.js"></script>
<script type="text/javascript">
	$(function() {

		$("button")
				.click(
						function() {

							var areaID = $(this).attr('value')
							//alert($(this).attr('value'));

									$.ajax({
										url : "fcWeatherList",
										type : "POST",
										data : areaID,

										success : function(data) {
											console.log("!!!!!!!!!!!!!!!!!!!!! 1) 받아온 FCList"+ data);
											
											var tag;
											var tag1;
											var tag2;
											var tag3;
											
											//3일 예보 날찌 
											var d1 = ${d};
											var d2 = ${d2};
											var d3 = ${d3};
											
											//현재 시간
											var nowTime = ${nowTime};
											
											var fwList = JSON.parse(data);
											console.log(fwList);
											console.log(JSON.stringify(fwList));

											console.log("1) "
													+ Object.keys(fwList));

											var keyList = Object.keys(fwList);

											for ( var i in keyList) {
												console.log("2) " + keyList[i]
														+ ",  data="
														+ fwList[keyList[i]]);

												var obj = fwList[keyList[i]];

												for ( var fcdatekey in obj) {
													console.log("3) "+ "예보날씨 날짜 순서: "+ fcdatekey + ", value: "+ obj[fcdatekey]);
													
													var obj2 = obj[fcdatekey];

													// 선택예보날씨에 나타낼 정보 변수  
													var d_name;
													var fcTime;
													var FTH;
													var fcDate;

													for ( var area in obj2) {
														console.log("4) "+ "예보날씨 내용 목록: "+ area+ ", value: "+ obj2[area]);

														if (area == 'district_name_step1') {
															d_name = obj2[area];
														} else if (area == 'fcTime') {
															fcTime = obj2[area];
														} else if (area == 'FTH') {
															FTH = obj2[area];
														} else if (area == 'fcDate') {
															fcDate = obj2[area];
														};
													}
														
													console.log("지역이름/"+ d_name + "/ 예보시간/"+fcTime+"/ 예보온도/" + FTH);
												
													tag = fcTime.substr(0,2)+'시<br>'+FTH.substr(0,2)+'℃';
													console.log("예보날씨 HTML 확인="+ tag);
														
														//예보날씨 예보시간별 <td>생성하기
														if (fcDate.substr(6,8) == d1) {
															
															if(parseInt(nowTime)>parseInt(fcTime)) {
																tag1 += '<td id ="DataTD_now">'+tag+'</td>';
																
															}else{
																tag1 += '<td id ="DataTD">'+tag+'</td>';
															}
															
														} else if(fcDate.substr(6,8) == d2) {
																
															tag2 += '<td id ="DataTD">'+tag+'</td>';
															
														} else if (fcDate.substr(6,8) == d3){
															
															tag3 += '<td id ="DataTD">'+tag+'</td>';
																
														};	
											}
										}
										console.log("예보날씨 HTML 확인="+ tag1);
										console.log("예보날씨 HTML 확인="+ tag2);
										console.log("예보날씨 HTML 확인="+ tag3);
										
										console.log("예보날씨지역 확인="+ d_name);
										
										$('#mainAreaName').html(d_name);
										$('#fcWeather_DAY_tr1').html(tag1);
										$('#fcWeather_DAY_tr2').html(tag2);
										$('#fcWeather_DAY_tr3').html(tag3);
									}});

						});

	});
</script>

</html>
