(ns differential-privacy-clj.core-test
  (:import [com.google.privacy.differentialprivacy Noise]
           [com.google.differentialprivacy SummaryOuterClass$MechanismType])
  (:require [clojure.test :refer :all]
            [differential-privacy-clj.core :as dp]))


;; default epsilon and delta for testing
(def e (Math/log 3))
(def d 0.1)


;; A noise object for testing. Doesn't add any noise.
(def zero-noise
  (reify Noise
    (^long addNoise [this ^long x ^int _ ^long _ ^double _ ^Double _]
      x)
    (^double addNoise [this ^double x ^int _ ^double _ ^double _ ^Double _]
      x)
    (getMechanismType [this]
      SummaryOuterClass$MechanismType/GAUSSIAN)))


(deftest mean-test
  (testing "bounded-mean"
    (is (<= 4.5
            (dp/bounded-mean (repeat 1000 5.0)
                             :epsilon e
                             :lower 0 :upper 10
                             :max-partitions-contributed 1
                             :max-contributions-per-partition 1)
            5.5))))

(deftest sum-test
  (testing "bounded-sum"
    (is (<= 5.2
            (dp/bounded-sum [1.0 2.0 3.0]
                            :epsilon (* 100 e)  ;; huge value for accuracy
                            :lower 0.0
                            :upper 10.0
                            :max-partitions-contributed 1
                            :noise (dp/laplace-noise))
            6.8))))

(deftest count-test
  (testing "count works with sequences"
    (is (<= 96
            (dp/count (repeat 100 :x)
                      :epsilon e :delta d
                      :max-partitions-contributed 1
                      :noise (dp/gaussian-noise))
            104)))
  (testing "count works with numbers"
    (is (<= 96
            (dp/count 100
                      :epsilon e
                      :max-partitions-contributed 1)
            104))))


(deftest zero-noise-testing
  (testing "zero noise bounded-mean"
    (is (=
         (dp/bounded-mean [1.0 2.0 3.0 4.0 5.0] :epsilon e :delta d
                          :lower 0 :upper 10
                          :max-partitions-contributed 1
                          :max-contributions-per-partition 1
                          :noise zero-noise)
         3.0)))
  (testing "zero noise bounded-sum"
    (is (=
         (dp/bounded-sum [1.0 2.0 3.0 4.0 5.3] :epsilon e :delta d
                         :lower 0 :upper 10
                         :max-partitions-contributed 1
                         :noise zero-noise)
         15.3)))
  (testing "zero noise bounded-mean with some values clipping"
    (is (=
         (dp/bounded-mean [1.0 2.0 3.1 4.0 5.0] :epsilon e :delta d
                          :lower 2.0 :upper 4.0
                          :max-partitions-contributed 1
                          :max-contributions-per-partition 1
                          :noise zero-noise)
         3.02)))
  (testing "zero noise bounded-sum with some values clipping"
    (is (= (dp/bounded-sum [1.0 -2.0 -3.0 4.2 15.3] :epsilon e :delta d
                           :lower 0 :upper 10
                           :max-partitions-contributed 1
                           :noise zero-noise)
           15.2)))
  (testing "zero noise count with a sequence"
    (is (<= 96
            (dp/count (repeat 100 :x)
                      :epsilon e :delta d
                      :max-partitions-contributed 1
                      :noise zero-noise)
            104)))
  (testing "zero noise count with a number"
    (is (<= 96
            (dp/count 100
                      :epsilon e :delta d
                      :max-partitions-contributed 1
                      :noise zero-noise)
            104))))


(deftest wrong-mean-keyword-arguments
  (testing "missing keyword arguments"
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-mean [1.0 2.0 3.0])))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-mean [1.0 2.0 3.0]
                                           :epsilon e)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-mean [1.0 2.0 3.0]
                                           :max-partitions-contributed 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-mean [1.0 2.0 3.0]
                                           :epsilon e
                                           :max-partitions-contributed 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-mean [1.0 2.0 3.0]
                                           :epsilon e
                                           :lower 0
                                           :max-partitions-contributed 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-mean [1.0 2.0 3.0]
                                           :epsilon e
                                           :lower 0 :upper 10
                                           :max-contributions-per-partition 1))))
  (testing "missing keyword arguments with extra keyword"
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-mean [1.0 2.0 3.0]
                                           :wrong-keyword 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-mean [1.0 2.0 3.0]
                                           :epsilon e
                                           :wrong-keyword 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-mean [1.0 2.0 3.0]
                                           :wrong-keyword 1
                                           :max-partitions-contributed 1))))
  (testing "extra keyword when all required keyword args are passed"
    (is (thrown-with-msg? IllegalArgumentException
                          #"Unexpected keyword arguments: :wrong-keyword"
                          (dp/bounded-mean [1.0 2.0 3.0]
                                           :epsilon e
                                           :lower 0 :upper 10
                                           :max-partitions-contributed 1
                                           :max-contributions-per-partition 1
                                           :wrong-keyword 1)))))


(deftest wrong-sum-keyword-arguments
  (testing "missing keyword arguments"
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-sum [1.0 2.0 3.0])))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-sum [1.0 2.0 3.0]
                                          :epsilon e)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-sum [1.0 2.0 3.0]
                                          :max-partitions-contributed 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-sum [1.0 2.0 3.0]
                                          :epsilon e
                                          :max-partitions-contributed 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-sum [1.0 2.0 3.0]
                                          :epsilon e
                                          :lower 0
                                          :max-partitions-contributed 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-sum [1.0 2.0 3.0]
                                          :epsilon e
                                          :lower 0 :upper 10))))
  (testing "missing keyword arguments with extra keyword"
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-sum [1.0 2.0 3.0]
                                          :wrong-keyword 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-sum [1.0 2.0 3.0]
                                          :epsilon e
                                          :wrong-keyword 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/bounded-sum [1.0 2.0 3.0]
                                          :wrong-keyword 1
                                          :max-partitions-contributed 1))))
  (testing "extra keyword when all required keyword args are passed"
    (is (thrown-with-msg? IllegalArgumentException
                          #"Unexpected keyword arguments: :wrong-keyword"
                          (dp/bounded-sum [1.0 2.0 3.0]
                                          :epsilon e
                                          :lower 0 :upper 10
                                          :max-partitions-contributed 1
                                          :wrong-keyword 1)))))


(deftest wrong-count-keyword-arguments
  (testing "missing keyword arguments"
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/count 10)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/count 10 :epsilon e)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/count 10 :max-partitions-contributed 1))))
  (testing "missing keyword arguments with extra keyword"
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/count 10 :wrong-keyword 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/count 10 :epsilon e :wrong-keyword 1)))
    (is (thrown-with-msg? IllegalArgumentException
                          #"Missing keyword arguments"
                          (dp/count 10 :max-partitions-contributed 1 :wrong-keyword 1))))
  (testing "extra keyword when all required keyword args are passed"
    (is (thrown-with-msg? IllegalArgumentException
                          #"Unexpected keyword arguments: :wrong-keyword"
                          (dp/count 10 :epsilon e :max-partitions-contributed 1
                                    :wrong-keyword 1)))))
