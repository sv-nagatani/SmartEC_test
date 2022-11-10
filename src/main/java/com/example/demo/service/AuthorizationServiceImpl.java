package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AccessAuthorization;
import com.example.demo.repository.AccessAuthorizationDao;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    private final AccessAuthorizationDao authDao;

    @Autowired
    public AuthorizationServiceImpl(AccessAuthorizationDao authDao) {
        this.authDao = authDao;
    }

    /**
     * 引数に渡された、RoleNameとURIの組み合わせがアクセス許可されているか判定する。
     * @param roleName
     * @param uri
     * @return boolean
     */
    @Override
    public boolean isAuthorized(String roleName, String uri) { // ---(1) アクセス許可されているか判定するメソッド

        if (roleName.isEmpty()) {
            throw new IllegalArgumentException("RoleNameが空です。");
        }

        if (uri.isEmpty()) {
            throw new IllegalArgumentException("URIが空です。");
        }

        //AccessAuthorization一件を取得 AccessAuthorizationが無ければ例外発生
        try {
            AccessAuthorization auth = authDao.find(roleName, uri); // ---(2) AccessAuthorizationインスタンスを取得

            if (auth != null) { // ---(3) アクセス許可
                return true;

            } else {            // ---(4) アクセス拒否
                return false;

            }
        } catch (EmptyResultDataAccessException e) { // ---(5) アクセス拒否
            return false;
        }
    }
}