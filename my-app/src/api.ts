import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { Floor, FloorEditAsset } from './type'

export const api = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({
    baseUrl: 'http://localhost:8080/',
  }),
  endpoints: (builder) => ({
    login: builder.mutation<number, number>({
      query: (id) => ({
        url: `/api/employees/${id}`,
        method: 'POST',
      }),
    }),
    createAssets: builder.mutation<
      number,
      { floorId: number; request: FloorEditAsset }
    >({
      query: (request) => ({
        url: `/api/floors/${request.floorId}/assets`,
        body: request.request,
        method: 'POST',
      }),
    }),
    getFoorById: builder.query<Floor, number>({
      query: (floorId) => ({
        url: `/api/floors/${floorId}`,
      }),
    }),
  }),
})

export const {
  useLoginMutation,
  useCreateAssetsMutation,
  useGetFoorByIdQuery,
} = api
