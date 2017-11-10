(ns com.example.hanoi-game-test
  (:require [clojure.test :refer [deftest testing is are] :as t]
            [clojure.repl :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as st]
            [com.example.hanoi-game :as hanoi]))

(deftest specs
  (st/instrument)
  (testing "generate game"
    (is (s/valid? ::hanoi/game (hanoi/create 4))
        (s/explain-str ::hanoi/game (hanoi/create 4))))
  (testing "pillars in wrong order"
    (is (not (s/valid? ::hanoi/game [[(hanoi/->tile 2) (hanoi/->tile 1) (hanoi/->tile 3)]
                                     []
                                     []]))))
  (testing "tiles-in-order? predicate"
    (are [result sizes] (= result (hanoi/tiles-in-desc-order? (map hanoi/->tile sizes)))
      false [10 20 30 40 50]
      false [1 4 3 2 5]
      true  [5 4 3 2 1]
      true  [50 40 30 20 10]
      true  [])
    (is (->> (s/coll-of ::hanoi/tile :distinct true :kind vector?)
             s/gen
             gen/sample
             (map hanoi/tiles-in-desc-order?)
             (every? (partial contains? #{true false})))))

  (st/check (st/enumerate-namespace 'com.example.hanoi-game))
  (st/unstrument))

(deftest towers-of-hanoi
  (testing "game factory"
    (is (= [[(hanoi/->tile 5) (hanoi/->tile 4) (hanoi/->tile 3) (hanoi/->tile 2) (hanoi/->tile 1)]
            []
            []]
           (hanoi/create 5))))
  (testing "make move"
    (let [game (hanoi/create 5)]
      (testing "simple move"
        (is (= [[(hanoi/->tile 5) (hanoi/->tile 4) (hanoi/->tile 3) (hanoi/->tile 2)]
                [(hanoi/->tile 1)]
                []]
               (hanoi/move game [0 1]))))
      (testing "several moves"
        (is (= [[(hanoi/->tile 5) (hanoi/->tile 4) (hanoi/->tile 3)]
                []
                [(hanoi/->tile 2) (hanoi/->tile 1)]]
               (-> game
                   (hanoi/move [0 1])
                   (hanoi/move [0 2])
                   (hanoi/move [1 2])))))))
  (testing "move validation"
    (testing "valid with spec"
      (let [game (hanoi/create 5)]
        (is (s/valid? ::hanoi/game (hanoi/move game [0 1])))))
    (testing "errors on invalid move"
      (let [game (-> (hanoi/create 3)
                     (hanoi/move [0 2])
                     (hanoi/move [0 1]))]
        (is (thrown? Throwable (hanoi/move game [1 2]))))))
  (testing "solution"
    (let [game-start (hanoi/create 3)]
      (let [moves (hanoi/solve game-start)]
        (is (= [[]
                []
                [(hanoi/->tile 3) (hanoi/->tile 2) (hanoi/->tile 1)]]
               (reduce hanoi/move game-start moves)))))))
