import { GridItem } from './useEditLayout'

export interface FloorEditAsset {
  id?: number
  width: number
  height: number
  assets: GridItem[]
}

export interface Floor {
  id: number
  building_id: number
  floor_number: number
  width: number
  height: number
  assets?: GridItem[]
}
