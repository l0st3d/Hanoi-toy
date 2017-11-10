(ns com.example.hanoi-game
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [com.example.hanoi-game :as hanoi]))

;; Specs

(s/check-asserts true)

(s/def ::tag #{:tile})
(s/def ::size pos-int?)
(s/def ::tile (s/keys :req [::tag ::size]))

(defn tiles-in-desc-order? [pillar]
  (or (empty? pillar)
      (->> pillar (map ::size) (apply >))))

(s/def ::pillar (s/and (s/coll-of ::tile) tiles-in-desc-order?))
(s/def ::game (s/tuple ::pillar ::pillar ::pillar)) ;NOTE these 2 specs assume game is of size 3 
(s/def ::pillar-index (s/and nat-int? #(< % 3)))
(s/def ::move (s/and (s/cat :from ::pillar-index
                            :to   ::pillar-index)
                     (fn [{:keys [from to]}] (not= from to))))

(defn valid-pillar?
  ([k [game move]]
   (when (and k move game)
     (< (move k) (count game))))
  ([k] (partial valid-pillar? k)))

(s/def ::game-and-move (s/and (s/tuple ::game ::move)
                              (valid-pillar? :from)
                              (valid-pillar? :to)))

(s/fdef ->tile :args (s/cat :size ::size) :ret ::tile)

(s/fdef create :args (s/cat :size pos-int?) :ret ::game)

(defn are-games-the-same-size?
  ([{[old-game] :args new-game :ret}]
   (are-games-the-same-size? old-game new-game))
  ([old-game new-game]
   (let [size-of (partial transduce (map count) + 0)]
     (= (size-of old-game) (size-of new-game)))))

(s/fdef move :args ::game-and-move :ret ::game :fn are-games-the-same-size?)

;; Model

(defn ->tile
  "Create a tile for the game."
  [size] 
  {::tag :tile ::size size})

(defn create
  "Create a new game."
  [max-disc-size]
  (let [number-of-towers 3]
    (into [(vec (map ->tile (range max-disc-size 0 -1)))] (repeat (dec number-of-towers) []))))

(defn move
  "Move a piece in the game."
  [game move]
  {:pre  [(s/assert ::game-and-move [game move])]
   :post [(s/assert ::game %) (are-games-the-same-size? game %)]}
  (let [{:keys [from to]} (s/conform ::move move)]
    (-> game
        (update from (comp vec butlast))
        (update to conj (last (get game from))))))

;; Solver

(defn solve [game]
  {:pre  [(s/assert ::game game) (every? empty? (rest game))]
   :post [(s/assert (s/coll-of ::move) %)]}
  (let [moves (transient [])
        solve (fn solve [n [a b c & cols]]
                (when (pos? n)
                  (solve (dec n) (into [a c b] cols))
                  (conj! moves [a c])
                  (solve (dec n) (into [b a c] cols))))]
    (solve (::size (ffirst game)) (range (count game)))
    (persistent! moves)))

;; printer

(defn game->str [game]
  (let [largest-tile (->> game (map first) (keep ::size) (reduce max 0))]
    (->> game
         (map #(->> % reverse (map ::size) (concat (repeat (- largest-tile (count %)) "."))))
         (apply map vector)
         (map #(interpose " " %))
         (interpose "\n")
         flatten
         (apply str))))

;; Global state of "the" game

(defonce game (atom (create 5)))

(comment

  (reset! game (create 5))
  
  (swap! game move [0 1])
  
  (swap! game #(reduce move % (solve %)))

  (println (game->str @game))

  )
