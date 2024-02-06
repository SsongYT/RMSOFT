<div align = center><img src="https://capsule-render.vercel.app/api?type=waving&color=auto&height=180&section=header&text=RMSOFT&fontSize=90"/></div>
<div align = right>
<h5>📅 2024.01.26 ~ 2024.02.06</h5>
</div>
<h1>📌ERD & REST API</h1>
<img width="450" alt="ERD" src="https://github.com/SsongYT/RMSOFT/assets/136442036/67fd4827-ff08-4afe-926f-d0688a4abc7e"/>
<img width="450" alt="REST API" src="https://github.com/SsongYT/RMSOFT/assets/136442036/59de2592-2105-4e06-a4a3-073107dd9983"/>
<br/>
<h1>📌테이블 정의서 & 기능 정의서</h1>
<img width="450" alt="테이블 정의서" src="https://github.com/SsongYT/RMSOFT/assets/136442036/77a8ac8c-7a2e-42f1-a856-9918baa7ccb0"/>
<img width="450" alt="기능정의서" src="https://github.com/SsongYT/RMSOFT/assets/136442036/5bf7b02f-ef72-4ea1-a7b4-a6546ac2dfa8"/>
<br/>
<h1>📌시스템 구성도</h1>
<img width="500" alt="시스템 구성도" src="https://github.com/SsongYT/RMSOFT/assets/136442036/a5f18651-bda3-4030-b818-54950f0a6261"/>
<br/>

***

<h1>🏆기능 리뷰</h1>
<h2>📍구독 정보 변경</h2>

```java
	//구독정보 변경
	@Transactional(rollbackOn = Exception.class)
	public ResponseData modifySubscribe(SubscribeModifyDTO subscribeModifyDTO, String userId) throws SQLException, IOException {
		ResponseData responseData = new ResponseData();
		//구독정보 인설트 해야하는지 유무
		boolean isInsert = false;
		
		//유저 PK 얻기
		int memberPk = memberMapper.selectMemberPkByUserId(userId);

		// 구독정보 업데이트용 VO 얻기
		SubscribeVO subscribeVO = subscribeMapper.selectSubscribeByMemberPk(memberPk);
		
		//결제 정보 인설트용 VO 생성
		PaymentVO paymentVO = new PaymentVO();
		
		// 새로운 구독정보 인설트용VO
		SubscribeVO subscribeModifyVO = new SubscribeVO();
		
		// subscribeModifyDTO에 따라 구분
		if(subscribeModifyDTO.getModifySolutionType() == null && subscribeModifyDTO.getModifyStartDate() == null) {
			// 취소
			// 구독을 다음달에 끝나도록 변경
			subscribeVO.setEnd_dt(now1Month());
			// 일반화를 위한 set
			subscribeVO.setModified_st('Y');
			// 결제정보 설정(기존 구독정보의 PK)
			paymentVO.setSubscribe_no(subscribeVO.getSubscribe_pk());
			
		} else if(subscribeModifyDTO.getModifySolutionType() == null && subscribeModifyDTO.getModifyStartDate() != null) {
			// 기간 변경
			//기존 구독정보 끝나는 날짜 변경
			subscribeVO.setEnd_dt(dateFormat(subscribeModifyDTO.getModifyEndDate()));
			// 일반화를 위한 set
			subscribeVO.setModified_st('Y');
			// 결제정보 설정(기존 구독정보의 PK)
			paymentVO.setSubscribe_no(subscribeVO.getSubscribe_pk());	
			
		} else if(subscribeModifyDTO.getModifySolutionType() != null) {
			// 변경할 솔루션의 정보 얻기
			SolutionVO modifySolutionVO = solutionMapper.selectSolutionBySolutionType(subscribeModifyDTO.getModifySolutionType());
			
			subscribeModifyVO.setMember_no(memberPk);
			subscribeModifyVO.setSolution_no(modifySolutionVO.getSolution_pk());
			subscribeModifyVO.setUse_st('N');
			
			if(subscribeModifyDTO.getModifyStartDate() == null) {
				// 혹시모를 예외 -> 디폴트값 : 다음달 설정
				subscribeVO.setEnd_dt(now1Month());	
				subscribeModifyVO.setStart_dt(now1Month());
				subscribeModifyVO.setEnd_dt(subscribeVO.getEnd_dt());
				
			} else if(subscribeModifyDTO.getModifyStartDate() != null) {		
				// 종류 변경 , 기간 변경 or 기간 변경X
				subscribeVO.setEnd_dt(dateFormat(subscribeModifyDTO.getModifyStartDate()));
				subscribeModifyVO.setStart_dt(dateFormat(subscribeModifyDTO.getModifyStartDate()));
				subscribeModifyVO.setEnd_dt(dateFormat(subscribeModifyDTO.getModifyEndDate()));
			}
			
			// 새로운 구독정보 인설트되기때문에 업데이트될 구독정보는 N으로 변경
			subscribeVO.setModified_st('N');
			
			isInsert = true;
		} 
		
		// 2. 기존 구독정보 업데이트
		if(subscribeMapper.updateSubscribeEndDT(subscribeVO) == 1) {
			
			if(isInsert) {
				// 1. 새로운 구독정보 인설트
				subscribeMapper.insertSubscribe(subscribeModifyVO);
				if(subscribeModifyVO.getSubscribe_pk() != 0) {
					// 결제정보 설정 (새로 인설트한 구독정보의 PK )
					paymentVO.setSubscribe_no(subscribeModifyVO.getSubscribe_pk());
				} else {
					// 구독정보 인설트 실패 예외
					throw new SQLException();
				}
			} 
			
			paymentVO.setPayment_type("카드");
			paymentVO.setPayment_st("Y");      //1(결제후-디폴트), 0(결제전)
			paymentVO.setPayment_price(subscribeModifyDTO.getComputePrice());
			paymentVO.setPayment_dt(LocalDateTime.now());
			// 3. 결제 정보 인설트
			if(paymentMapper.insertPayment(paymentVO) == 1) {
				responseData.setCode(ResponseDataEnum.basic_true.getCode());
				responseData.setMessages(ResponseDataEnum.basic_true.getMessages());
				
			} else {
				// 결제 정보 인설트 실패 예외
				throw new SQLException();
			}
			
		} else {
			// 구독정보 업데이트 실패 예외
			throw new SQLException();
		}
		
		return responseData;
	}
```
<h3>💡평가</h3>

> 중복 코드를 없애고 싶어 모든 경우(취소,종류변경,기간변경,종류 및 기간변경)를 일반화 하여
>
> 처리하는 메서드를 만들게 된 것 같다.
>
> 이런 코드는 유지 및 보수도 힘들고, REST API에 의도에 적합한 것 인지 의문이 들었다.
>
> 다음에 구현하게 된다면 기능은 분리하면서 중복코드는 없애는 방향으로 생각하여 만들겠다.

</br>
<h2>📍구독 정보 변경전 금액정보 계산</h2>

```java
// 금액 계산하는 메서드
	public int computePrice(SubscribeModifyDTO subscribeModifyDTO, SubscribeVO subscribeVO, SolutionVO solutionVO) throws SQLException, IOException {
		int computePrice = -1;
		boolean isCompute = false;
		//타입 변경 유무 확인
		if(subscribeModifyDTO.getModifySolutionType() == null || subscribeModifyDTO.getModifySolutionType().equals(solutionVO.getSolution_type())) {
			//타입 변경 X
			subscribeModifyDTO.setModifySolutionType(solutionVO.getSolution_type());
			subscribeModifyDTO.setModifySolutionPrice(solutionVO.getSolution_price());
		} else {
			//타입 변경
			// 변경할 솔루션의 정보
			SolutionVO modifySolutionVO = solutionMapper.selectSolutionBySolutionType(subscribeModifyDTO.getModifySolutionType());
			// 얻어온 솔루션가격 set
			subscribeModifyDTO.setModifySolutionPrice(modifySolutionVO.getSolution_price());
			
			isCompute = true;
		}
		
		LocalDateTime modifyStartDate = null;
		LocalDateTime modifyEndDate = null;
		
		//기간 변경유무
		if(subscribeModifyDTO.getModifyEndDate() == null || dateFormat(subscribeModifyDTO.getModifyEndDate()).equals(subscribeVO.getEnd_dt())) {
			// 기간 변경 X
			modifyStartDate = now1Month();
			modifyEndDate = subscribeVO.getEnd_dt();
		} else {
			//시간 변경
			modifyStartDate = dateFormat(subscribeModifyDTO.getModifyStartDate());
			modifyEndDate = dateFormat(subscribeModifyDTO.getModifyEndDate());	
			isCompute = true;
		}

		// 금액 계산
		if(isCompute) {
			int compareMonthA = compareMonth(subscribeVO.getEnd_dt(), modifyStartDate);
			int compareMonthB = compareMonth(modifyEndDate, subscribeVO.getEnd_dt());
			
			int price = subscribeModifyDTO.getModifySolutionPrice() - solutionVO.getSolution_price();

			computePrice = (compareMonthA * price) + (compareMonthB * subscribeModifyDTO.getModifySolutionPrice());

		}

		return computePrice;
	}
```
<h3>💡평가</h3>

> 시작할때는 간단하게 계산이 될줄 알았는데, 로직이 생각보다 복잡했고
>
> 만든후에도 TEST 과정에서 에러가 발생하여 테이블 수정이 발생하여 시간을 많이 쓰게 된 점이 아쉽다.
>
> 결제기능은 없지만 미리 고려하여 추가결제에 대한 정보를 산출하고 retur하게 만든것은 잘한것같다.

***

</br>
<h2>📋총평</h2>

> 스프링, sts3를 사용하다 스프링부트로 만들게 되어 시작할때는 인텔리J에 gradle로 만들어 보려고 했지만
>
> 생각보다 어려운점이 많아 sts4로 만들게 되었는데, 유행하는 개발툴은 미리 경험해 두는것이 좋을것 같다는 생각을 했고
>
> 테이블 구성 및 로직 구성도 설계 단계에서 완벽하지 못하여 중간 수정을 하게 된 점도 아쉽게 생각한다.
>
> 또 REST API 보안 관련하여 HTTPS, API TOKEN 이나 API Key 적용하여 만들고 싶었지만 적용하지 못하였고, 
>
> exception과 errorcode의 효과적 관리를 하고 싶어 enum을 사용하여 구현을 하였는데,
>
> http상태코드나 exception 이해가 부족함을 느꼈고 효율적으로 구현되지 못한것 같아 아쉽고
>
> entity를 활용이나 시큐리티 활용을 하지 못했다. 이부분들을 보강하여 ver2.0을 만들어봐야겠다.

***

