(ns advanced-particles.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.records.vector2d :refer :all]
            [basic-particles.protocols.vector :as v]
            [basic-ces.core :refer :all]))

(defn wind [entities]
  (let [entities-with-mass (filter-entities entities :mass :velocity)]
    (reduce (fn [agg [id components]]
              (let [mass (:mass components)
                    wind (->Vector2D -0.2 0)
                    acceleration (v/divide wind mass)]
                (update-in agg [id :velocity] v/add acceleration)))
            entities
            entities-with-mass)))

(defn gravity [entities]
  (let [entities-with-mass (filter-entities entities :mass :velocity)]
    (reduce (fn [agg [id components]]
              (let [mass (:mass components)
                    gravity (->Vector2D 0 0.51)
                    acceleration (v/divide gravity mass)]
                (update-in agg [id :velocity] v/add acceleration)))
            entities
            entities-with-mass)))

(defn mover [entities]
  (let [movers (filter-entities entities :position :velocity)]
    (reduce (fn [agg [id components]]
              (let [velocity (:velocity components)]
                (update-in agg [id :position] v/add velocity)))
            entities
            movers)))

(defn degeneration [entities]
  (let [degenerative-entities (filter-entities entities :lifespan)]
    (reduce (fn [agg [id components]]
              (let [lifespan (:lifespan components)
                    new-lifespan (- lifespan 2)]
                 (if (< new-lifespan 0)
                   (dissoc agg id)
                   (assoc-in agg [id :lifespan] new-lifespan))))
            entities
            degenerative-entities)))

(defn renderer [entities]
  (doseq [[id components] (filter-entities entities :renderable)]
    (let [render-func (:renderable components)]
      (render-func components)))
  entities)

(defn render-particle [components]
  (let [position (:position components)
        lifespan (:lifespan components)]
    (q/fill 20 lifespan)
    (q/ellipse (:x position) (:y position) 10 10)))

(defn create-particle [position]
  (entity {:label :particle
           :velocity (->Vector2D (q/random -1 3) (q/random 1 4))
           :position position
           :mass 1
           :lifespan 255
           :renderable render-particle}))

(defn setup []
  (q/frame-rate 60)
  (merge {} (create-particle (->Vector2D (/ (q/width) 2) 200))))

(defn prog-loop [entities]
  (q/background 240)
  (println entities)
  (-> entities
      (gravity)
      (wind)
      (mover)
      (degeneration)
      (renderer)))

(defn -main []
  (q/defsketch basic-particles
    :title "Advanced Particles"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
