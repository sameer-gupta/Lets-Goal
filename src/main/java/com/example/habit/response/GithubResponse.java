package com.example.habit.response;

import com.example.habit.bo.CommitBO;
import com.example.habit.bo.ParentBO;
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
public class GithubResponse {

    String sha;

    String node_id;
    CommitBO commit;
    String url;
    String html_url;
    String comments_url;
}