package org.swdc.rmdisk.core.repo.filters;

import org.swdc.data.SQLFactory;
import org.swdc.data.SQLParams;
import org.swdc.rmdisk.core.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.StringJoiner;

public class FilteredUserQueryCountFactory implements SQLFactory {
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
        if (sqlParams.containsKey("status")) {
            joiner.add(" status = :status ");
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT count(id) FROM User WHERE ");
        queryBuilder.append(joiner);
        if (sqlParams.containsKey("order")) {
            queryBuilder.append(" ORDER BY " + sqlParams.get("order"));
        }

        TypedQuery<Long> query = em.createQuery(queryBuilder.toString(), Long.class);
        for (String key : sqlParams.getKeys()) {
            if (key.equals("keyword")) {
                query.setParameter("keyword", "%" + sqlParams.get(key) + "%");
                continue;
            } else if (key.equals("order") || key.equals("pageNo") || key.equals("pageSize")) {
                continue;
            }
            query.setParameter(key, sqlParams.get(key));
        }

        return query;
    }
}
