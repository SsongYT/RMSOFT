package com.rmsoft.app.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubscribeVO {
	private int subscribe_pk;
	private int member_no;
	private int solution_no;
	private LocalDateTime start_dt;
	private LocalDateTime end_dt;
	private char use_st;
	private char modified_st;
	private LocalDateTime create_dt;
	private LocalDateTime modified_dt;
}
