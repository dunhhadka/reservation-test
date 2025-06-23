import { useMemo, useState } from 'react'
import { useGetFoorByIdQuery } from './api'
import { LayoutState } from './useEditLayout'
import { v4 as uuidv4 } from 'uuid'
import { Layer, Rect, Stage, Text } from 'react-konva'
import {
  getColor,
  getIcon,
  GRID_COLS,
  GRID_ROWS,
  GRID_SIZE,
} from './RoomLayoutEditor'
import { Button } from 'antd'

const BookingSeatPage = () => {
  const { data: floor, isLoading: isFloorLoading } = useGetFoorByIdQuery(1)
  const [selectedSeats, setSelectedSeats] = useState<any[]>([])

  const layout = useMemo((): LayoutState => {
    if (floor?.assets) {
      return {
        id: uuidv4(),
        state: floor.assets,
      }
    }
    return {
      id: uuidv4(),
      state: [],
    }
  }, [floor])

  const handleSeatClick = (obj: any) => {
    if (obj.type === 'SEAT') {
      setSelectedSeats((prev) => {
        const exists = prev.find((seat) => seat.id_holder === obj.id_holder)
        if (exists) {
          return prev.filter((seat) => seat.id_holder !== obj.id_holder)
        } else {
          return [...prev, obj]
        }
      })
    }
  }

  const isSelected = (obj: any) =>
    selectedSeats.some((seat) => seat.id_holder === obj.id_holder)

  return (
    <div style={{ padding: 16 }}>
      <Stage width={GRID_COLS * GRID_SIZE} height={GRID_ROWS * GRID_SIZE}>
        <Layer>
          {Array.from({ length: GRID_ROWS }).map((_, row) =>
            Array.from({ length: GRID_COLS }).map((_, col) => (
              <Rect
                key={`grid-${row}-${col}`}
                x={col * GRID_SIZE}
                y={row * GRID_SIZE}
                width={GRID_SIZE}
                height={GRID_SIZE}
                stroke="#e0e0e0"
              />
            ))
          )}
        </Layer>
        <Layer>
          {layout.state.map((obj) => (
            <Rect
              key={`obj-${obj.id_holder}`}
              x={obj.grid_x + 4}
              y={obj.grid_y + 4}
              width={obj.width - 8}
              height={obj.height - 8}
              fill={getColor(obj.type)}
              cornerRadius={8}
              shadowBlur={8}
              shadowColor="rgba(0,0,0,0.2)"
            />
          ))}
          {layout.state.map((obj) => (
            <Text
              key={`icon-${obj.id_holder}`}
              text={getIcon(obj.type)}
              fontSize={GRID_SIZE / 2}
              fill="#fff"
              x={obj.grid_x + GRID_SIZE / 4 - 3}
              y={obj.grid_y + GRID_SIZE / 4 - 1}
              onClick={() => handleSeatClick(obj)}
              style={{ cursor: 'pointer' }}
            />
          ))}
          {/* Tick xanh khi seat được chọn */}
          {selectedSeats.map((obj) => (
            <Text
              key={`tick-${obj.id_holder}`}
              text="✔"
              fontSize={GRID_SIZE / 2}
              fill="#52c41a"
              x={obj.grid_x + GRID_SIZE / 4 - 3}
              y={obj.grid_y + GRID_SIZE / 4 - 1}
            />
          ))}
        </Layer>
      </Stage>

      <div style={{ marginTop: 16 }}>
        <Button
          type="primary"
          danger
          disabled={selectedSeats.length === 0}
          onClick={() => {
            console.log('Booking seats:', selectedSeats)
            // Gọi API booking ở đây
          }}
        >
          Book {selectedSeats.length} Seat{selectedSeats.length > 1 ? 's' : ''}
        </Button>
      </div>
    </div>
  )
}

export default BookingSeatPage
