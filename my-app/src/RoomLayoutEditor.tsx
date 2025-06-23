import React, { useRef, useEffect, useState } from 'react'
import { Stage, Layer, Rect, Text } from 'react-konva'
import { useDrag, useDrop } from 'react-dnd'
import { Button, Modal, Tooltip } from 'antd'
import {
  SaveOutlined,
  UndoOutlined,
  RightOutlined,
  UserOutlined,
  TeamOutlined,
  CoffeeOutlined,
} from '@ant-design/icons'
import { v4 as uuidv4 } from 'uuid'
import { GridItem, useEditLayout, type GridItemType } from './useEditLayout'
import { useGetFoorByIdQuery } from './api'

export const GRID_SIZE = 40
export const GRID_ROWS = 10
export const GRID_COLS = 20
const DragItemType = 'ItemType'

export const getColor = (type: GridItemType) => {
  switch (type) {
    case 'SEAT':
      return '#900'
    case 'ROOM':
      return '#006d75'
    case 'PANTRY':
      return '#d46b08'
    default:
      return '#555'
  }
}

export const getIcon = (type: GridItemType) => {
  switch (type) {
    case 'SEAT':
      return '👤'
    case 'ROOM':
      return '👥'
    case 'PANTRY':
      return '☕'
    default:
      return ''
  }
}

interface ItemValue {
  type: string
  width: number
  height: number
}

const DragItem = ({
  type,
  label,
  icon,
  color,
  hoverColor,
}: {
  type: GridItemType
  label: string
  icon: React.ReactNode
  color: string
  hoverColor: string
}) => {
  const [{ isDragging }, drag] = useDrag(() => ({
    type: DragItemType,
    item: {
      type,
      width: GRID_SIZE,
      height: GRID_SIZE,
    },
    collect: (monitor) => ({ isDragging: !!monitor.isDragging() }),
  }))

  return drag(
    <div
      style={{
        margin: '8px 0',
        opacity: isDragging ? 0.4 : 1,
        transform: isDragging ? 'scale(0.95)' : 'scale(1)',
        transition: 'all 0.2s',
      }}
    >
      <Tooltip title={label}>
        <Button
          icon={icon}
          style={{
            background: color,
            color: '#fff',
            border: 'none',
            borderRadius: '8px',
            width: '100%',
            height: '40px',
            boxShadow: '0 2px 6px rgba(0,0,0,0.2)',
          }}
          onMouseEnter={(e) => (e.currentTarget.style.background = hoverColor)}
          onMouseLeave={(e) => (e.currentTarget.style.background = color)}
        />
      </Tooltip>
    </div>
  )
}

const RoomLayoutEditor = () => {
  const { data: floor, isLoading: isFloorLoading } = useGetFoorByIdQuery(1)

  const {
    autoSaveLayout,
    isEditing,
    setIsEditing,
    layout,
    undoLayout,
    setLayout,
    isFirst,
    nextLayout,
    save,
  } = useEditLayout({
    id: uuidv4(),
    state: [],
  })

  console.log(floor, layout, floor?.assets)

  const [editGridItem, setEditGridItem] = useState<GridItem | undefined>(
    undefined
  )
  const [openEditItemModal, setOpenEditItemModal] = useState<boolean>(false)

  const stageContainerRef = useRef<HTMLDivElement>(null)

  const [{ isOver }, dropRef] = useDrop(() => ({
    accept: DragItemType,
    drop: (item: ItemValue, monitor) => {
      const offset = monitor.getClientOffset()
      if (!offset || !stageContainerRef.current) return

      const containerRect = stageContainerRef.current.getBoundingClientRect()
      const x = offset.x - containerRect.left
      const y = offset.y - containerRect.top

      const snappedX = Math.floor(x / GRID_SIZE) * GRID_SIZE
      const snappedY = Math.floor(y / GRID_SIZE) * GRID_SIZE

      if (
        layout.state.some(
          (item) => item.grid_x === snappedX && item.grid_y === snappedY
        )
      )
        return

      setLayout((prev) => ({
        id: uuidv4(),
        state: [
          ...prev.state,
          {
            id: Date.now(),
            grid_x: snappedX,
            grid_y: snappedY,
            type: item.type as GridItemType,
            width: item.width,
            height: item.height,
            id_holder: uuidv4(),
          },
        ],
      }))

      if (!isEditing) setIsEditing(true)
    },
    collect: (monitor) => ({ isOver: !!monitor.isOver() }),
  }))

  useEffect(() => {
    if (isEditing) autoSaveLayout(layout)
  }, [layout, isEditing, autoSaveLayout, isFloorLoading])

  useEffect(() => {
    if (floor?.assets) {
      setLayout({
        id: uuidv4(),
        state: floor.assets.map((item) => ({ ...item, id_holder: uuidv4() })),
      })
    }
  }, [floor, setLayout])

  const handleDeleteItem = (id: number | string) => {
    setLayout((prev) => ({
      id: uuidv4(),
      state: prev.state.filter((item) => item.id_holder !== id),
    }))
  }

  return (
    <div
      style={{
        display: 'flex',
        padding: '16px',
        gap: '16px',
        background: '#f0f2f5',
        minHeight: '100vh',
      }}
    >
      <div>
        <div style={{ marginBottom: '12px', display: 'flex', gap: '8px' }}>
          <Tooltip title="Save Layout">
            <Button
              icon={<SaveOutlined />}
              style={{
                background: '#900',
                color: '#fff',
                border: 'none',
                borderRadius: '8px',
              }}
              onMouseEnter={(e) => (e.currentTarget.style.background = '#c00')}
              onMouseLeave={(e) => (e.currentTarget.style.background = '#900')}
              onClick={save}
            />
          </Tooltip>
          <Tooltip title="Undo">
            <Button
              icon={<UndoOutlined />}
              style={{
                background: '#900',
                color: '#fff',
                border: 'none',
                borderRadius: '8px',
              }}
              onMouseEnter={(e) => (e.currentTarget.style.background = '#c00')}
              onMouseLeave={(e) => (e.currentTarget.style.background = '#900')}
              onClick={undoLayout}
              disabled={isFirst}
            />
          </Tooltip>
          <Tooltip title="Next">
            <Button
              icon={<RightOutlined />}
              style={{
                background: '#900',
                color: '#fff',
                border: 'none',
                borderRadius: '8px',
              }}
              onMouseEnter={(e) => (e.currentTarget.style.background = '#c00')}
              onMouseLeave={(e) => (e.currentTarget.style.background = '#900')}
              onClick={nextLayout}
            />
          </Tooltip>
        </div>

        {dropRef(
          <div
            ref={stageContainerRef}
            style={{
              border: isOver ? '1px dashed #900' : '1px solid #d9d9d9',
              borderRadius: '8px',
              background: '#fff',
              boxShadow: isOver
                ? '0 0 20px rgba(144,0,0,0.4)'
                : '0 4px 12px rgba(0,0,0,0.1)',
              transition: 'all 0.2s',
              display: 'inline-block',
            }}
          >
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
                    onClick={() => {
                      setEditGridItem(obj)
                      setOpenEditItemModal(true)
                    }}
                  />
                ))}
              </Layer>
            </Stage>
          </div>
        )}
      </div>

      <div
        style={{
          width: '140px',
          display: 'flex',
          flexDirection: 'column',
          gap: '12px',
        }}
      >
        <DragItem
          type="SEAT"
          label="Seat (1x1)"
          icon={<UserOutlined />}
          color="#900"
          hoverColor="#c00"
        />
        <DragItem
          type="ROOM"
          label="Meeting Room (3x3)"
          icon={<TeamOutlined />}
          color="#006d75"
          hoverColor="#08979c"
        />
        <DragItem
          type="PANTRY"
          label="Pantry (2x2)"
          icon={<CoffeeOutlined />}
          color="#d46b08"
          hoverColor="#fa8c16"
        />
      </div>
      {editGridItem && openEditItemModal && (
        <Modal
          open={openEditItemModal}
          title="Edit Item"
          onCancel={() => {
            setEditGridItem(undefined)
            setOpenEditItemModal(false)
          }}
          footer={[
            <Button
              key="delete"
              danger
              type="primary"
              onClick={() => {
                handleDeleteItem(editGridItem.id_holder ?? 0)
                setEditGridItem(undefined)
                setOpenEditItemModal(false)
              }}
              style={{
                background: '#ff4d4f',
                borderColor: '#ff4d4f',
                boxShadow: '0 2px 6px rgba(255,77,79,0.4)',
              }}
            >
              Delete
            </Button>,
            <Button
              key="cancel"
              onClick={() => {
                setEditGridItem(undefined)
                setOpenEditItemModal(false)
              }}
            >
              Cancel
            </Button>,
          ]}
        >
          <p>Type: {editGridItem.type}</p>
          <p>
            Position: ({editGridItem.grid_x}, {editGridItem.grid_y})
          </p>
        </Modal>
      )}
    </div>
  )
}

export default RoomLayoutEditor
