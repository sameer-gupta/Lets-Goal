package com.example.habit.dao;

import com.example.habit.entity.Column;
import com.example.habit.entity.Goal;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sameer Gupta
 */
@Repository
public class GoalDAO {


    @Autowired
    @Qualifier("mysql")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insertActor;


    public Goal insertGoal(Goal goal) throws Exception {

        BigInteger number = insertJDBC(goal);
        goal.setId(number);
        return goal;
    }

    private BigInteger insertJDBC(Goal goal) {

        String query = DatabaseUtil.getInsertQuery(Goal.class);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(query, DatabaseUtil.getParameterSource(goal), keyHolder,
                new String[]{"id"});
        return new BigInteger(keyHolder.getKey().toString());
    }


    public int makeItActive(String orderRefId){
        String query = "update goal set is_active = :isActive where order_ref_id = :order_ref_id";
        return jdbcTemplate.update(query, new MapSqlParameterSource().addValue("order_ref_id", orderRefId).addValue("isActive",true));
    }

    public Goal getById(BigInteger id){
        Goal object = new Goal();
        try {
            MapSqlParameterSource map = new MapSqlParameterSource();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select * from goal where id = :id order by id desc");
            map.addValue("id", id);
            List<Goal> goals = jdbcTemplate.query(queryBuilder.toString(), map, new BeanPropertyRowMapper<>(Goal.class));
            return CollectionUtils.isEmpty(goals) ? object : goals.get(0);
        } catch (Exception exception) {
            return object;
        }
    }

    public List<Goal> getByUserId(BigInteger id){

            MapSqlParameterSource map = new MapSqlParameterSource();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select * from goal where user_id = :id order by id desc");
            map.addValue("id", id);
            List<Goal> goals = jdbcTemplate.query(queryBuilder.toString(), map, new BeanPropertyRowMapper<>(Goal.class));
           return goals;
    }

    public int update(Goal goal) throws Exception{

        String sql = "UPDATE goal SET user_id = :userId, category_id = :categoryId, target_date = :targetDate, " +
                "target_amount = :targetAmount, target_goal = :targetGoal, completed_goal = :completedGoal, is_active = :isActive, " +
                "is_deleted = :isDeleted, order_ref_id = :orderRefId, updated_at = :updatedAt where id = :id";

        MapSqlParameterSource param = new MapSqlParameterSource();

        param.addValue("userId", goal.getUserId());
        param.addValue("categoryId", goal.getCategoryId());
        param.addValue("targetDate", goal.getTargetDate());
        param.addValue("targetAmount", goal.getTargetAmount());
        param.addValue("targetGoal", goal.getTargetGoal());
        param.addValue("completedGoal", goal.getCompletedGoal());
        param.addValue("isActive", goal.getIsActive());
        param.addValue("isDeleted", goal.getIsDeleted());
        param.addValue("orderRefId", goal.getOrderRefId());
        param.addValue("updatedAt", goal.getUpdatedAt());
        param.addValue("id", goal.getId());

        return jdbcTemplate.update(sql, param);
    }


}
