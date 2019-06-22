package com.example.habit.request;

import com.example.habit.bo.MessageBO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


/**
 * @author Sameer Gupta
 */

@Setter
@Getter
@ToString
public class whatsappCallbackRequest implements Serializable {

    List<MessageBO> messages;
}
