package com.example.habit.response;

import com.example.habit.bo.StepDataPacketBO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Sameer Gupta
 */
@Setter
@Getter
@ToString
public class FitbitResponse {
    @JsonProperty
    @SerializedName("activities-steps")
    List<StepDataPacketBO> activitiesSteps;
}
