(ns dark-forces.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.records.vector2d :refer :all]
            [basic-ces.core :refer :all]
            [dark-forces.systems :refer :all]))

(defn setup []
  (q/frame-rate 60)
  (merge {} (entity {:label :particle-system
                     :position (->Vector2D (/ (q/width) 2) 200)
                     :particle-emitter true})))

(defn render-particle [components]
  (let [position (:position components)
        lifespan (:lifespan components)
        angle (:angle components)]
    (q/stroke 0 lifespan)
    (q/rect-mode :center)
    (q/fill 175 lifespan)
    (q/with-translation [(:x position) (:y position)]
      (q/with-rotation [angle]
        (q/rect 0 0 8 8)))))

(defn create-particle-entity [position]
  (entity {:label :particle
           :velocity (->Vector2D (q/random -3 -1) (q/random -3 -1))
           :position position
           :mass 1
           :lifespan 255
           :renderable render-particle
           :angle 0}))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (particle-emitter create-particle-entity)
      (gravity)
      (wind)
      (attract-and-repel-with-mouse)
      (mover)
      (angular-rotation)
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

