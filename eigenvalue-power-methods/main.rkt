#lang racket

(require yaml)
(require math/matrix)
(require "math.rkt")
(require "applications.rkt")
(require "power-method.rkt")
(require "inverse-power-method.rkt")

(define source (make-parameter "assignment.yml"))
(define dest (make-parameter "destination.yml"))

;; Разбор аргументов командной строки
(define parser
  (command-line
   #:once-each
   [("-s" "--source") SOURCE
    "Path to your assignment file"
    (source SOURCE)]
   [("-d" "--destination") DESTINATION
    "Output destination"
    (dest DESTINATION)]

   #:args () (void)))

;; Печать информации о собственных числах
(define-syntax-rule (print-eigenvalues purpose v A) 
  (let-values ([(l b iterations) v])
    (display (string-append purpose ":\n"))
    (printf "\tсобственное число: ~s\n" l)
    (printf "\tсобственный вектор: ~v\n" b)
    (printf "\tколичество итераций: ~s\n" iterations)
    (printf "\tневязка: ~s\n" (residual A l b))))

;; Обработка исходных данных
(define (process-eigenvalues)
  (let* ([config-get ((curry hash-ref) (with-input-from-file (source) read-yaml))]
         [A (list*->matrix (config-get "матрица" '()))]
         [eps (config-get "точность" 0.1)]
         [approx (config-get "λ0" 0)]
         [initial (config-get "начальный_вектор" (λ () (build-list (length A) 0)))])
    (print-eigenvalues "λ1" (power-iteration A initial eps) A)
    ;;(print-eigenvalues "λ2" (power-iteration-second A initial eps) A)
    (print-eigenvalues "λ2 (метод исчерпывания)" (power-iteration-exhausting A initial eps) A)
    (print-eigenvalues "ближайшее к λ0" (inverse-power-iteration A initial approx eps) A)
    (print-eigenvalues "минимальное λ" (min-eigenvalue A initial eps 10) A)
    (print-eigenvalues "максимальное λ" (max-eigenvalue A initial eps 10) A)
    (print-eigenvalues "λk" (min-absolute-eigenvalue A initial eps) A)))

(process-eigenvalues)