package com.mc.imageranker;

import org.springframework.data.repository.CrudRepository;

public interface FriendsRepository extends CrudRepository<Friends, Long> {
    Iterable<Friends> findAllByOrderByRankDesc();
}
