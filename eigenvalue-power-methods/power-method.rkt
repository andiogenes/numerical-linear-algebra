#lang racket

(require math/matrix)
(require "math.rkt")

(provide power-iteration)
(provide power-iteration-second)
(provide power-iteration-exhausting)

;; Степенной метод
(define (power-iteration A initial eps)
  (let* ([b_k (->col-matrix initial)] ;; x^k
         [b_k+1 b_k]                  ;; x^(k+1)
         [l_max +inf.0]               ;; lambda^(k+1)
         [l_prev 0]                   ;; lambda^k
         [iterations 0])              ;; количество итераций

    (for ([i (in-naturals)])
          #:break (< (abs (- l_max l_prev)) eps)      ;; Условие завершения: delta < eps
      (let ([tmp b_k+1]
            [tmp_norm (norm b_k+1)])
        (set! b_k (matrix-scale tmp (/ 1 tmp_norm)))  ;; Нормирование x^k
        (set! b_k+1 (matrix* A b_k))                  ;; x^(k+1) = Ax^k
        (set! l_prev l_max)                           ;; запись предыдущего lambda^k в аккумулятор 
        (set! l_max (/ (dot-prod b_k+1 b_k) (dot-prod b_k b_k)))  ;; вычисление lambda^(k+1)
        (set! iterations (add1 iterations)))) ;; увеличение числа итераций

    (values l_max b_k iterations)))

;; Степенной метод для 2-го максимального по модулю собственного числа
;; Через метод сдерживания e1
(define (power-iteration-second A initial eps)
  (let* ([e (take-second (power-iteration A initial eps))]
         [g (take-second (power-iteration (matrix-transpose A) initial eps))]
         [ortonormalize (λ (x) 
           (matrix-scale e (/ (dot-prod x g) (dot-prod e g))))]
         [initial-matrix (->col-matrix initial)]
         [b_k (matrix- initial-matrix (ortonormalize initial-matrix))]
         [b_k+1 b_k]
         [l_max +inf.0]
         [l_prev 0]
         [iterations 0])

    (for ([i (in-naturals)])
          #:break (< (abs (- l_max l_prev)) eps)
      (let ([tmp b_k+1]
            [tmp_norm (norm b_k+1)])
        (set! b_k (matrix-scale tmp (/ 1 tmp_norm)))
        (set! b_k (matrix- b_k (ortonormalize b_k)))
        (set! b_k+1 (matrix* A b_k))
        (set! l_prev l_max)
        (set! l_max (/ (dot-prod b_k+1 b_k) (dot-prod b_k b_k)))
        (set! iterations (add1 iterations))))

    (values l_max b_k iterations)))

;; Поиск 2-го максимального по модулю собственного числа через метод исчерпывания
(define (power-iteration-exhausting A initial eps)
  (let-values ([(l e _) (power-iteration A initial eps)]
               [(__ g ___) (power-iteration (matrix-transpose A) initial eps)])
    (let* ([coef (/ l (dot-prod g e))]
           [B (matrix- A (matrix-scale (matrix* e (matrix-transpose g)) coef))]) ;; вычисление B
      (power-iteration B initial eps))))  ;; степенной метод от B

(define-syntax-rule (take-second v)
  (let-values ([(first second third) v])
    second))