package com.zeotap.ruleengineapp.repository;

import com.zeotap.ruleengineapp.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
}
