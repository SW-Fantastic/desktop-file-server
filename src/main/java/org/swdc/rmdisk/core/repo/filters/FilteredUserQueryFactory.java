package org.swdc.rmdisk.core.repo.filters;

import jakarta.persistence.TypedQuery;
import org.swdc.data.SQLFactory;
import org.swdc.data.SQLParams;
import org.swdc.rmdisk.core.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.StringJoiner;

/**
 * 创建一个查询用户信息的工厂类，根据传入的参数动态生成SQL语句。
 * 用于对用户信息进行筛选查询。
 */
public class FilteredUserQueryFactory implements SQLFactory {

    @Override
    public Query createQuery(EntityManager em, SQLParams sqlParams) {

        StringJoiner joiner = new StringJoiner(" AND ");

        if (sqlParams.containsKey("keyword")) {
            joiner.add(" ( username LIKE :keyword OR nickname LIKE :keyword ) ");
        }
        if (sqlParams.containsKey("start")) {
            joiner.add(" createdOn >= :start ");
        }
        if (sqlParams.containsKey("end")) {
            joiner.add(" createdOn <= :end ");
        }
        if (sqlParams.containsKey("groupId")) {
            joiner.add(" group.id = :groupId ");
        }
        if (sqlParams.containsKey("state")) {
            joiner.add(" state = :state ");
        }

        StringBuilder queryBuilder = new StringBuilder("FROM User WHERE ");
        queryBuilder.append(joiner);
        if (sqlParams.containsKey("order")) {
            queryBuilder.append(" ORDER BY " + sqlParams.get("order"));
        }

        TypedQuery<User> query = em.createQuery(queryBuilder.toString(), User.class);
        for (String key : sqlParams.getKeys()) {
            if (key.equals("keyword")) {
                query.setParameter("keyword", "%" + sqlParams.get(key) + "%");
                continue;
            } else if (key.equals("order") || key.equals("pageNo") || key.equals("pageSize")) {
                continue;
            }
            query.setParameter(key, sqlParams.get(key));
        }

        if (sqlParams.containsKey("pageNo") && sqlParams.containsKey("pageSize")) {
            int pageNo = sqlParams.get("pageNo");
            int pageSize = sqlParams.get("pageSize");
            query.setFirstResult(pageNo * pageSize);
            query.setMaxResults(pageSize);
        }

        return query;
    }

}
