package org.swdc.rmdisk.core.repo.filters;

import org.swdc.data.SQLFactory;
import org.swdc.data.SQLParams;
import org.swdc.rmdisk.core.entity.UserRegisterRequest;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.StringJoiner;

public class FilteredRegisterQueryFactory implements SQLFactory {

    @Override
    public Query createQuery(EntityManager entityManager, SQLParams sqlParams) {

        StringJoiner joiner = new StringJoiner(" AND ");
        if (sqlParams.containsKey("keyword")) {
            joiner.add("(name LIKE :keyword OR nickname LIKE :keyword)");
        }
        if (sqlParams.containsKey("state")) {
            joiner.add("state = :state");
        }
        if (sqlParams.containsKey("start")) {
            joiner.add("start >= :start");
        }
        if (sqlParams.containsKey("end")) {
            joiner.add("end <= :end");
        }

        StringBuilder queryBuilder = new StringBuilder("FROM UserRegisterRequest WHERE ");
        queryBuilder.append(joiner);
        if (sqlParams.containsKey("order")) {
            queryBuilder.append(" ORDER BY ").append(sqlParams.get("order").toString());
        }

        Query query = entityManager.createQuery(queryBuilder.toString(), UserRegisterRequest.class);
        for (String key : sqlParams.getKeys()) {
            if (key.equals("keyword")) {
                query.setParameter(key, "%" + sqlParams.get(key) + "%");
                continue;
            } else if (key.equals("order") || key.equals("pageSize") || key.equals("pageNo")) {
                continue;
            }
            query.setParameter(key, sqlParams.get(key));
        }

        if (sqlParams.containsKey("pageSize") && sqlParams.containsKey("pageNo")) {
            int pageSize = sqlParams.get("pageSize");
            int pageNo = sqlParams.get("pageNo");
            query.setFirstResult((pageNo - 1) * pageSize);
            query.setMaxResults(pageSize);
        }

        return query;
    }

}
