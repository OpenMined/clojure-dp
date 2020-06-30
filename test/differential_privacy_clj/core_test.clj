(ns differential-privacy-clj.core-test
  (:require [clojure.test :refer :all]
            [differential-privacy-clj.core :as dp]))


;; default epsilon for testing
(def e (Math/log 3))


(deftest sum-test
  (testing "bounded-sum"
    (is (<= 5.2
            (dp/bounded-sum [1.0 2.0 3.0]
                            :epsilon (* 100 e)  ;; huge value for accuracy
                            :lower 0.0
                            :upper 10.0
                            :max-partitions-contributed 1)
            6.8))))

(deftest count-test
  (testing "count works with sequences"
    (is (<= 96
            (dp/count (repeat 100 :x)
                      :epsilon e
                      :max-partitions-contributed 1)
            104)))
  (testing "count works with numbers"
    (is (<= 96
            (dp/count 100
                      :epsilon e
                      :max-partitions-contributed 1)
            104))))

(deftest mean-test
  (testing "bounded-mean"
    (is (<= 4.5
            (dp/bounded-mean (repeatedly 1000 #(rand 10.0))
                             :epsilon e
                             :lower 0 :upper 10
                             :max-partitions-contributed 1
                             :max-contributions-per-partition 1)
            5.5))))
