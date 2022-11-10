package com.example.demo.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;

import com.example.demo.service.AccountUserDetailsService;

@Configuration
@EnableWebSecurity // --- (1)
@EnableGlobalMethodSecurity(prePostEnabled = true) // 追記 --- (1) メソッド認可処理を有効化
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountUserDetailsService userDetailsService;

    @Autowired                                      // 追記
    AccessDecisionVoter<FilterInvocation> myVoter;  // 追記

    public AccessDecisionManager createAccessDecisionManager() { // 追記
        return new AffirmativeBased(Arrays.asList(myVoter)); // 追記 ---(1) 認可処理はAffirmativeBased、投票処理はMyVoterを使用する。
    } // 追記

    PasswordEncoder passwordEncoder() {
        //BCryptアルゴリズムを使用してパスワードのハッシュ化を行う
        return new BCryptPasswordEncoder(); // --- (2) BCryptアルゴリズムを使用
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // AuthenticationManagerBuilderに、実装したUserDetailsServiceを設定する
        auth.userDetailsService(userDetailsService)     // --- (3) 作成したUserDetailsServiceを設定
                .passwordEncoder(passwordEncoder());    // --- (2) パスワードのハッシュ化方法を指定(BCryptアルゴリズム)
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 認可の設定
        http.exceptionHandling()
                .accessDeniedPage("/accessDeniedPage")
            .and()
            .authorizeRequests()
                .antMatchers("/**").authenticated() // 修正
                    .accessDecisionManager(createAccessDecisionManager()); // 追記 ---(2) すべてのアクセスにおいて、認可処理の適用

        // ログイン設定
        http.formLogin()                                // --- (6) フォーム認証の有効化
                .loginPage("/loginForm")                // --- (7) ログインフォームを表示するパス
                .loginProcessingUrl("/authenticate")    // --- (8) フォーム認証処理のパス
                .usernameParameter("userName")          // --- (9) ユーザ名のリクエストパラメータ名
                .passwordParameter("password")          // --- (10) パスワードのリクエストパラメータ名
                .defaultSuccessUrl("/home")             // --- (11) 認証成功時に遷移するデフォルトのパス
                .failureUrl("/loginForm?error=true");   // --- (12) 認証失敗時に遷移するパス

        // ログアウト設定
        http.logout()
                .logoutSuccessUrl("/loginForm")         // --- (13) ログアウト成功時に遷移するパス
                .permitAll();                           // --- (14) 全ユーザに対してアクセスを許可
    }
}