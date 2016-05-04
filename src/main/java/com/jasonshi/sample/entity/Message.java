package com.jasonshi.sample.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This is a common class which should be used for both client and server side.
 * @author shishuwu
 */
@Data
@AllArgsConstructor
public class Message implements Serializable{

	private static final long serialVersionUID = -2729306424618819143L;
	private int id;
	private String content;
}
