package com.nighthawk.spring_portfolio.mvc.stock;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// JPA is an object-relational mapping (ORM) to persistent data, originally relational databases (SQL). Today JPA implementations has been extended for NoSQL.
public interface StockJpaRepository extends JpaRepository<Stock, Long> {
    // JPA has many built in methods, these few have been prototyped for this application
    void save(String Joke);
    List<Stock> findAllByOrderByJokeAsc();
    List<Stock> findByJokeIgnoreCase(String joke);
}
