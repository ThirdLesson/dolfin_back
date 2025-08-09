package org.scoula.domain.exchange.entity;

public enum Type {
	BASE,     //  기준 환율
	SEND,     //  송금을 보낼때
	RECEIVE,  //  송금을 받을때
	GETCASH,  //  현찰 구매
	SELLCASH; //  현찰 판매
}
