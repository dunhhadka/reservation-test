import { useCallback, useEffect, useState } from 'react'
import { useCreateAssetsMutation } from './api'
import { GRID_SIZE } from './RoomLayoutEditor'

export interface LayoutState {
  state: GridItem[]
  id: string
}

export interface GridItem {
  id?: number | string
  id_holder?: string
  grid_x: number
  grid_y: number
  type: GridItemType
  width: number
  height: number
  code?: string
}

export type GridItemType = 'SEAT' | 'PANTRY' | 'ROOM'

export const useEditLayout = (initLayout: LayoutState) => {
  const [editHistories, setEditHistories] = useState<LayoutState[]>([
    initLayout,
  ])
  const [currentLayoutIndex, setCurrentLayoutIndex] = useState<number>(0)

  const [isEditing, setIsEditing] = useState<boolean>(false)

  const [layout, setLayout] = useState<LayoutState>(initLayout)

  const [createAssets] = useCreateAssetsMutation()

  const autoSaveLayout = useCallback(
    (state: LayoutState) => {
      if (!isEditing && !!editHistories.length) return

      if (editHistories.some((item) => item.id === state.id)) return

      if (currentLayoutIndex > editHistories.length - 1) {
        return
      }

      if (currentLayoutIndex === editHistories.length - 1) {
        setEditHistories((prev) => [...prev, state])
        setCurrentLayoutIndex((prev) => prev + 1)
        return
      }

      setEditHistories((prev) => [
        ...prev.slice(0, currentLayoutIndex + 1),
        state,
      ])
      setCurrentLayoutIndex((prev) => prev + 1)
    },
    [currentLayoutIndex, editHistories, isEditing]
  )

  const undoLayout = useCallback(() => {
    if (currentLayoutIndex === 0) return

    setLayout(editHistories[currentLayoutIndex - 1])
    setCurrentLayoutIndex((prev) => prev - 1)
  }, [currentLayoutIndex, editHistories])

  const nextLayout = useCallback(() => {
    if (currentLayoutIndex === editHistories.length - 1) return

    setLayout(editHistories[currentLayoutIndex + 1])
    setCurrentLayoutIndex((prev) => prev + 1)
  }, [currentLayoutIndex, editHistories])

  const save = useCallback(async () => {
    console.log('save')

    const response = await createAssets({
      floorId: 1,
      request: {
        width: 20 * GRID_SIZE,
        height: 10 * GRID_SIZE,
        assets: layout.state,
      },
    })
  }, [createAssets, layout.state])

  // useEffect(() => {
  //   setLayout(initLayout)
  // }, [initLayout])

  return {
    editHistories,
    currentLayoutIndex,
    isEditing,
    layout,
    save,
    undoLayout,
    setLayout,
    autoSaveLayout,
    setIsEditing,
    nextLayout,
    isFirst: currentLayoutIndex === 0,
    isLast: currentLayoutIndex === editHistories.length - 1,
  }
}
