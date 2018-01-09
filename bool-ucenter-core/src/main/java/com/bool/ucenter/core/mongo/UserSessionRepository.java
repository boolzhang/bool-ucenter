package com.bool.ucenter.core.mongo;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface UserSessionRepository extends MongoRepository<UserSession,String>{

	 @Query("{ 'userId' : ?0 }")
	 List<UserSession> findByUserId(int userId);
}
