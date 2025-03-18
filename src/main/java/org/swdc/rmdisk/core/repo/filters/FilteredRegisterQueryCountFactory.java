package org.swdc.rmdisk.core.repo.filters;

import org.swdc.data.SQLFactory;
import org.swdc.data.SQLParams;
import org.swdc.rmdisk.core.entity.UserRegisterRequest;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.StringJoiner;

public class FilteredRegisterQueryCountFactory implements SQLFactory {

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

        StringBuilder queryBuilder = new StringBuilder("SELECT count(id) FROM UserRegisterRequest WHERE ");
        queryBuilder.append(joiner);

        Query query = entityManager.createQuery(queryBuilder.toString(), Long.class);
        for (String key : sqlParams.getKeys()) {
            if (key.equals("keyword")) {
                query.setParameter(key, "%" + sqlParams.get(key) + "%");
                continue;
            }
            query.setParameter(key, sqlParams.get(key));
        }


        return query;
    }

}
