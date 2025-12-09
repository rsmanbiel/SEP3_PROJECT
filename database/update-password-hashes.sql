-- Update all user password hashes to the verified BCrypt hash for "password123"
-- This hash is compatible with Spring Security BCryptPasswordEncoder (strength 10)

UPDATE users SET password_hash = '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW' WHERE username = 'admin';
UPDATE users SET password_hash = '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW' WHERE username = 'supervisor1';
UPDATE users SET password_hash = '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW' WHERE username = 'supervisor2';
UPDATE users SET password_hash = '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW' WHERE username = 'operator1';
UPDATE users SET password_hash = '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW' WHERE username = 'operator2';
UPDATE users SET password_hash = '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW' WHERE username = 'operator3';
UPDATE users SET password_hash = '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW' WHERE username = 'customer1';
UPDATE users SET password_hash = '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW' WHERE username = 'customer2';
UPDATE users SET password_hash = '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW' WHERE username = 'customer3';

-- Verify the update
SELECT username, LEFT(password_hash, 30) || '...' as hash_preview FROM users ORDER BY username;

