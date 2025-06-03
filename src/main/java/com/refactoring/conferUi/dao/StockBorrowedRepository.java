package com.refactoring.conferUi.dao;

import com.refactoring.conferUi.Model.Entity.StockBorrowed;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StockBorrowedRepository extends JpaRepository<StockBorrowed, Integer> {
}
