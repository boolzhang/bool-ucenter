package com.bool.ucenter.core.mongo;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserSessionRepository extends MongoRepository<UserSession,String>{

}
