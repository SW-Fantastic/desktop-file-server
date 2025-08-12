package org.swdc.rmdisk.core.repo.filters;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.swdc.data.SQLFactory;
import org.swdc.data.SQLParams;

import java.util.StringJoiner;

public class FilteredActivityQueryCountFactory implements SQLFactory {

    @Override
    public Query createQuery(EntityManager entityManager, SQLParams sqlParams) {
        StringJoiner joiner = new StringJoiner(" AND ");
        if (sqlParams.containsKey("keyword")) {
            joiner.add(" (user.name LIKE :keyword OR user.nickname LIKE :keyword) ");
        }
        if (sqlParams.containsKey("operation")) {
            joiner.add(" operation = :operation ");
        }
        if (sqlParams.containsKey("start")) {
            joiner.add(" createdOn >= :start ");
        }
        if (sqlParams.containsKey("end")) {
            joiner.add(" createdOn <= :end" );
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT count(id) FROM Activity WHERE ");
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
