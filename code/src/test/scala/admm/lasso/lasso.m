% Generates and solves a small dense Lasso problem, and then
% writes the solution to the text file 'lasso_sol.csv'
%
% specifically minimizes norm(A*x-b,2) + lambda*norm(x,1) over x
% where A is an m by n matrix
% b is a column vector of length m
% x is a column vector of length n
% A(i,j) = cos(i*j)
% b = A*sin(1:n)
% and lambda is a regularization tradeoff parameter

n = 5;
m = 25;
A = cos((1:m)'*(1:n));
b = A*sin(1:n)';
lambda = 1.0;
cvx_begin quiet
    variable x(n)
    minimize norm(A*x - b,2) + lambda*norm(x,1)
cvx_end
csvwrite('lasso_sol.csv',x)